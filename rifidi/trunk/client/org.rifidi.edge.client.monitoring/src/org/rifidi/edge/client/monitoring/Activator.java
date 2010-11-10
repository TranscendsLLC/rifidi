package org.rifidi.edge.client.monitoring;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.rifidi.edge.client.monitoring.console.ConsoleJMSDestMonitor;
import org.rifidi.edge.client.monitoring.tagview.TagViewJMSDestMonitor;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.rifidi.edge.client.monitoring";
	// The shared instance
	private static Activator plugin;
	// The JMS monitor for the Console
	private ConsoleJMSDestMonitor consoleMonitor;
	// The JMS monitor for the tags
	private TagViewJMSDestMonitor tagMonitor;
	
	public static String IMG_REFRESH = "refresh";
	public static String IMG_PAUSE= "pause";

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		this.consoleMonitor = new ConsoleJMSDestMonitor();
		this.tagMonitor = new TagViewJMSDestMonitor();
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				consoleMonitor.start();
				tagMonitor.start();
				
			}
		});
		t.start();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				consoleMonitor.stop();
				tagMonitor.stop();
				
			}
		});
		t.start();
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
	 * 
	 * @return the JMS Listener for the console
	 */
	public ConsoleJMSDestMonitor getConsoleMonitor() {
		return this.consoleMonitor;
	}

	/**
	 * 
	 * @return The JMS monitor for the tags
	 */
	public TagViewJMSDestMonitor getTagMonitor() {
		return tagMonitor;
	}
	
	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#initializeImageRegistry(org.eclipse
	 * .jface.resource.ImageRegistry)
	 */
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		reg.put(IMG_REFRESH, getImageDescriptor("IMG/arrow_refresh.png"));
		reg.put(IMG_PAUSE, getImageDescriptor("IMG/control_pause_blue.png"));
	}
}
