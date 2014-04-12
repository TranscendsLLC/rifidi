/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
package org.rifidi.edge.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This service auto-loads applications found in a file specified by the
 * 'org.rifidi.edge.applications.initial' system variable that is located in the
 * file specified by the 'org.rifidi.edge.applications' variable. By default,
 * this is /applications/default.ini.
 * 
 * The file is a list of comma separated application names. Lines that start
 * with # are ignored. Linebreaks are ok, as long as they don't occur in the
 * middle of an application name.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * @since 1.1
 * 
 */
public class ApplicationLoader {
	/** The provisioning service that actually loads the bundles */
	private volatile ProvisioningService provisioningService = null;
	/** The name of the file that contains the applications to load */
	private volatile String defaultApplicationsFileName = null;
	/** The path to the applications folder */
	private volatile String pathToAppFolder = null;
	/** The logger for this class */
	private final static Log logger = LogFactory
			.getLog(ApplicationLoader.class);

	/**
	 * Called by spring to set the provisioning service
	 * 
	 * @param provisioningService
	 *            The service that loads bundles using OBR
	 */
	public void setProvisioningService(ProvisioningService provisioningService) {
		this.provisioningService = provisioningService;
		if (defaultApplicationsFileName != null && pathToAppFolder != null) {
			loadDefaultApplications();
		}
	}

	/**
	 * Called by spring to supply the file name of the file that contains the
	 * applications to load
	 * 
	 * @param defaultApplicationsFileName
	 *            The name of file that contains the default applications
	 */
	public void setDefaultApplicationsFileName(
			String defaultApplicationsFileName) {
		this.defaultApplicationsFileName = defaultApplicationsFileName;
		if (provisioningService != null && pathToAppFolder != null) {
			loadDefaultApplications();
		}
	}

	/**
	 * Called by spring to set the path to the applications folder
	 * 
	 * @param pathToAppFolder
	 *            The relative path to the applications folder
	 */
	public void setPathToAppFolder(String pathToAppFolder) {
		this.pathToAppFolder = pathToAppFolder;
		if (provisioningService != null && defaultApplicationsFileName != null) {
			loadDefaultApplications();
		}
	}

	/**
	 * Once all three of the above variables have been set, parse the file and
	 * load the application
	 */
	private void loadDefaultApplications() {
		// the path to the file
		String path = pathToAppFolder + File.separator
				+ defaultApplicationsFileName;
		File f = new File(path);
		Set<String> applicationsToLoad = new HashSet<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			// read in the file line by line
			String line = reader.readLine();
			while (line != null) {
				// process the line
				applicationsToLoad.addAll(processLine(line));
				line = reader.readLine();
			}
			// once all lines have been read in, load the applicatiosn
			loadApps(applicationsToLoad);
		} catch (FileNotFoundException e) {
			logger.warn("File not found: " + path);
		} catch (IOException e) {
			logger.warn("There was a problem when reading the file: " + path
					+ " No applications were loaded");
		}
	}

	/**
	 * A helper method to load all the applications specified in the file
	 * 
	 * @param applicationsToLoad
	 */
	private void loadApps(final Set<String> applicationsToLoad) {
		final ProvisioningService provisioningService = this.provisioningService;
		// load the files in a thread so we don't occupy the spring thread for
		// too long
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// step through the applications one-by-one and load them.
				for (String appName : applicationsToLoad) {
					try {
						Set<String> bundles = provisioningService
								.provision(appName);
						for (String s : bundles) {
							logger.info("bundle loaded: " + s);
						}
						logger.info("Application loaded: " + appName);
					} catch (CannotProvisionException e) {
						logger.warn("Cannot load the following application: "
								+ appName);
						e.printStackTrace();
					}
				}

			}
		});
		thread.start();

	}

	/**
	 * A helper method to process one line of the input file
	 * 
	 * @param line
	 *            the line to processes
	 * @return A set of all the application names found in that file
	 */
	private Set<String> processLine(String line) {
		Set<String> appNames = new HashSet<String>();
		//ignore lines that begin with #
		if (line.startsWith("#")) {
			return appNames;
		}
		//break up all application names using a comma 
		String[] splitLine = line.split(",");
		for (String app : splitLine) {
			appNames.add(app.trim());
		}
		return appNames;
	}

}
