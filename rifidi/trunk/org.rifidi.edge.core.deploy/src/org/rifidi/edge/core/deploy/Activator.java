package org.rifidi.edge.core.deploy;

import java.io.File;
import java.util.ArrayList;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.rifidi.edge.core.deploy.service.impl.DeployServiceImpl;

/**
 * This is the Activator of the Deploy Bundle
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class Activator implements BundleActivator {

	private DeployServiceImpl deployServiceImpl;
	private ArrayList<String> paths;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		System.out.println("== Bundle " + this.getClass().getName()
				+ " loaded ==");

		System.out.println("Starting: DeploymentService");
		deployServiceImpl = new DeployServiceImpl(context);
		paths = new ArrayList<String>();
		// TODO Change Directory to something with more sense
		// paths.add("C:\\temp");
		paths.add("./plugins/extensions/");
		 File file = new File("test");
		 System.out.println(file.getAbsolutePath());
		// System.out.println(file.getCanonicalPath());
		deployServiceImpl.add(paths);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		System.out.println("Stopping: DeploymentService");
		deployServiceImpl.remove(paths);
		System.out.println("== Bundle " + this.getClass().getName()
				+ " stopped ==");
	}

}
