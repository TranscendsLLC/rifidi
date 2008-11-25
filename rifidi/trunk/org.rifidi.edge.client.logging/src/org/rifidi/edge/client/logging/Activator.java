package org.rifidi.edge.client.logging;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.rifidi.edge.client.connections.edgeserver.registry.EdgeServerConnectionRegistry;
import org.rifidi.edge.client.logging.impl.TagLogger;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.rifidi.edge.client.logging";

	// The shared instance
	private static Activator plugin;

	private EdgeServerConnectionRegistry edgeServerConnectionRegistry;

	private TagLogger tagLogger;

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
		System.out.println("== Bundle " + getClass().getName() + " started ==");

		context.registerService(TagLogger.class.getName(), new TagLogger(
				"taglog.xml"), null);

		super.start(context);
		plugin = this;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		System.out.println("== Bundle " + getClass().getName() + " stoped ==");
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

}
