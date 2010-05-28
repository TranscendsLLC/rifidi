package org.rifidi.edge.logging;

import java.io.File;
import java.net.URI;

import org.apache.log4j.PropertyConfigurator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * This bundle looks for a system property called 'org.rifidi.edge.logging'
 * which contains the path to a log4j properties file. If the file cannot be
 * found, it uses the default log4j configuration in the classpath.
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
		System.out.println("START LOGGING BUNDLE");
		System.out.println("ALL RIFIDI CONFIGURATION PATHS RELATIVE TO : "
				+ System.getProperty("user.dir"));
		System.setProperty("org.rifidi.edge.applications", "applications");
		String s = "file:" + System.getProperty("user.dir")
				+ System.getProperty("file.separator")
				+ System.getProperty("org.rifidi.edge.logging");
		URI uri = new URI(s);

		if (new File(uri).exists()) {
			PropertyConfigurator.configure(uri.toURL());
			System.out.println("Using log4j configuration at: " + s);
		} else {
			System.out.println("Cannot find log properties file at " + s
					+ " Using default log4j properties");
			PropertyConfigurator.configure(getClass().getResource(
					"/log4j.properties"));
		}

	}

	@Override
	public void stop(BundleContext arg0) throws Exception {
		// TODO Auto-generated method stub

	}

}
