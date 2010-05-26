/*
 * 
 * ProvisioningServiceImpl.java
 *  
 * Created:     Oct 1st, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
package org.rifidi.edge.core.services.provisioning.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.obr.RepositoryAdmin;
import org.osgi.service.obr.Requirement;
import org.osgi.service.obr.Resolver;
import org.osgi.service.obr.Resource;
import org.rifidi.edge.core.services.provisioning.ProvisioningService;
import org.rifidi.edge.core.services.provisioning.exceptions.CannotProvisionException;

/**
 * This is an implementation of the Provisioning Service
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ProvisioningServiceImpl implements ProvisioningService {

	/** The OBR Repository Admin */
	private volatile RepositoryAdmin repositoryAdmin;
	/** Logger for this class */
	private static final Log logger = LogFactory
			.getLog(ProvisioningServiceImpl.class);
	private volatile String pathToAppFolder;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.services.provisioning.ProvisioningService#provision
	 * (java.lang.String)
	 */
	@Override
	public Set<String> provision(String application)
			throws CannotProvisionException {
		return provision(application, 0);
	}

	private Set<String> provision(String application, int attempt)
			throws CannotProvisionException {
		// retrun values
		Set<String> retVal = new HashSet<String>();
		try {
			// add the repository specified by the path
			repositoryAdmin.addRepository(getRepository(application));
		} catch (Exception e) {
			throw new CannotProvisionException(e);
		}

		Resolver resolver = repositoryAdmin.resolver();

		// Add every resource found in the repository
		Resource[] resources = repositoryAdmin
				.discoverResources("(symbolicname=*)");
		for (Resource r : resources) {
			try {
				resolver.add(r);
			} catch (Exception e) {
				throw new CannotProvisionException(e);
			}
			retVal.add(r.getSymbolicName());

		}
		// if everyting is ok, deploy!
		if (resolver.resolve()) {
			try {
				resolver.deploy(true);
			} catch (IllegalStateException e) {
				if (attempt < 3) {
					logger.warn("Illegal State Exception when loading "
							+ application + ", attempt again");
					return provision(application, attempt++);
				} else {
					logger.warn("Tried to load " + application
							+ " application 3 times. Giving up.");
					throw new CannotProvisionException(e);
				}
			} catch (Throwable e) {
				throw new CannotProvisionException(e);
			}
			return retVal;
		} else {
			// If there were some problems, don't deploy.
			Requirement[] reqs = resolver.getUnsatisfiedRequirements();
			for (int i = 0; i < reqs.length; i++) {
				logger.warn("Unable to resolve: " + reqs[i].getFilter());
			}
			throw new CannotProvisionException("Unresolved Dependencies");
		}
	}

	/**
	 * Private helper method to formulate URL.
	 * 
	 * TODO: eventually make this more generic so that an application could be
	 * loaded via any URL
	 * 
	 * @param application
	 *            The name of the application to install
	 * @return A URL to the XML file
	 * @throws MalformedURLException
	 */
	private URL getRepository(String application) throws MalformedURLException,
			FileNotFoundException {
		String path = this.pathToAppFolder + File.separator + application
				+ File.separator + "repository.xml";

		File f = new File(path);
		if (!f.exists()) {
			logger.error("Repository file does not exist: "
					+ f.getAbsolutePath());
			throw new FileNotFoundException();
		}

		try {
			return f.toURI().toURL();
		} catch (MalformedURLException e) {
			logger.error("Invalid URL " + f.getAbsolutePath());
			throw e;
		}
	}

	/**
	 * Called by spring
	 * 
	 * @param repoistoryAdmin
	 */
	public void setRepositoryAdmin(RepositoryAdmin repoistoryAdmin) {
		this.repositoryAdmin = repoistoryAdmin;
	}

	/**
	 * Called by spring
	 * 
	 * @param pathToAppFolder
	 */
	public void setPathToAppFolder(String pathToAppFolder) {
		logger.info("Path to applications foler: " + pathToAppFolder);
		this.pathToAppFolder = pathToAppFolder;
	}

}
