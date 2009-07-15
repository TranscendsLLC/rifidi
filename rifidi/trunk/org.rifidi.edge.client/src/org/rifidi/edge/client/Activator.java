package org.rifidi.edge.client;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.rifidi.edge.client";

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		//TODO: this REALLY sucks
//		for(Bundle bundle:context.getBundles()){
//			if(bundle.getSymbolicName().equals("org.eclipse.equinox.weaving.aspectj")){
//				System.out.println(bundle.getSymbolicName());
//				bundle.start();
//			}
//			if(bundle.getSymbolicName().equals("org.springframework.bundle.osgi.extender")){
//				System.out.println(bundle.getSymbolicName());
//				bundle.start();
//			}
//			if(bundle.getSymbolicName().equals("org.springframework.bundle.spring.aspects")){
//				System.out.println(bundle.getSymbolicName());
//				bundle.start();
//			}
//			if(bundle.getSymbolicName().equals("org.rifidi.org.springframework.bundle.spring")){
//				System.out.println(bundle.getSymbolicName());
//				bundle.start();
//			}
//			
//		}
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
