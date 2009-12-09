package org.rifidi.edge.logging;

import java.io.File;

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
		String s = System.getProperty("org.rifidi.edge.logging");

		if (s != null && new File(s).exists()) {
			PropertyConfigurator.configure(s);
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
