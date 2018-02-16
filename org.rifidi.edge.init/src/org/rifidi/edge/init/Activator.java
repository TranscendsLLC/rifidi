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
package org.rifidi.edge.init;

import java.io.File;
import java.net.URI;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.PropertyConfigurator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

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

			boolean failoverEnabled = false;
			String primary = System.getProperty("org.rifidi.failover.primary");
			if (primary != null && !primary.equals("")) {
				failoverEnabled = true;
			}
			if (failoverEnabled) {
				System.out.println("Primary: " + primary);
				Integer failoverTrySeconds = Integer.parseInt(System
						.getProperty("org.rifidi.failover.frequency"));
				Integer numFailsRequired = Integer.parseInt(System
						.getProperty("org.rifidi.failover.failurecount"));
				for (int numFails = 0; numFails < numFailsRequired;) {
					CloseableHttpClient httpclient = null;
					try {
						Thread.sleep(failoverTrySeconds * 1000);
						httpclient = HttpClients.createDefault();
						int CONNECTION_TIMEOUT_MS = 5 * 1000; // Timeout in millis.
						RequestConfig requestConfig = RequestConfig.custom()
						    .setConnectionRequestTimeout(CONNECTION_TIMEOUT_MS)
						    .setConnectTimeout(CONNECTION_TIMEOUT_MS)
						    .setSocketTimeout(CONNECTION_TIMEOUT_MS)
						    .build();

						HttpGet httpGet = new HttpGet("http://" + primary + "/ping");
						httpGet.setConfig(requestConfig);
						httpclient.execute(httpGet);

						System.out.println("Response received from " + primary + ", sleeping for " + failoverTrySeconds + " seconds");
						// Got past the get()...reset the counter
						if (numFails > 0) {
							System.out.println("Request to primary " + primary + " succeeded after previous failure, resetting the counter");
							numFails = 0;
						}
					} catch (InterruptedException e) {
						System.out.println("Interrupted Exception -- trying again");
						// Do nothing...wasn't a network failure
					} catch (Exception e) {
						// Exception happened, could not connect to
						numFails++;
						System.out.println("Failed to connect to primary " + primary
								+ ", incrementing counter: " + numFails + " number of fails required: " + numFailsRequired);
					} finally {
						if(httpclient!=null) {
							httpclient.close();
						}
					}
				}

				// TODO: Call the primary here, get a response.
			}

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
