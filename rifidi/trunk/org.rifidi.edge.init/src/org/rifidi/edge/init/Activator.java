/*
 *  Activator.java
 *
 *  Created:	April 16th, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.init;

import java.io.File;
import java.net.URI;

import org.apache.log4j.PropertyConfigurator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.rifidi.edge.init.utility.URIUtility;

/**
 * 
 * This bundle is the initialization bundle for the edge server. It sets up
 * paths and variables needed by other parts of the system.
 * 
 * It sets the 'org.rifidi.home' variable to the the 'user.dir' variable if
 * 'org.rifidi.home' is not already set.
 * 
 * It also looks for a system property called 'org.rifidi.edge.logging' which
 * contains the path to a log4j properties file. If the file cannot be found, it
 * uses the default log4j configuration in the classpath.
 * 
 * This bundle should be started along with the log4j bundle at start level 2 to
 * ensure that everything is logged properly.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class Activator implements BundleActivator {

	@Override
	public void start(BundleContext arg0) throws Exception {

		try {
			System.out.println("START RIFIDI INITIALIZTION BUNDLE");

			// look up the path to the rifidi home directory
			String rifidiHome = System.getProperty("org.rifidi.home");

			// if it's null, set it to the "user.dir" directory
			if (rifidiHome == null) {
				String userDir = System.getProperty("user.dir");
				System.setProperty("org.rifidi.home", userDir);
				rifidiHome = System.getProperty("org.rifidi.home");
			}
			System.out.println("ALL RIFIDI CONFIGURATION PATHS RELATIVE TO : "
					+ System.getProperty("org.rifidi.home"));

			// set the path to the applications folder
			System.setProperty("org.rifidi.edge.applications", "applications");

			// set up logging
			String slash = "";
			if (rifidiHome.charAt(0) != '/') {
				slash = "/";
			}
			String s = "file:" + slash + rifidiHome
					+ System.getProperty("file.separator")
					+ System.getProperty("org.rifidi.edge.logging");
			// We have to do this bit of idiocy to get URIs to work with windows
			// backslashes and "Documents and Settings".
			URI uri = new URI(URIUtility.createURI(s));

			if (new File(uri).exists()) {
				PropertyConfigurator.configure(uri.toURL());
				System.out.println("Using log4j configuration at: " + s);
			} else {
				System.out.println("Cannot find log properties file at " + s
						+ " Using default log4j properties");
				PropertyConfigurator.configure(getClass().getResource(
						"/log4j.properties"));
			}

			System.setProperty("activemq.base", rifidiHome + File.separator
					+ "config");
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	@Override
	public void stop(BundleContext arg0) throws Exception {
		// TODO Auto-generated method stub

	}

}
