package org.rifidi.edge.client.ale.ecspecview;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.rifidi.edge.client.ale.logicalreaders.ALELRService;
import org.rifidi.edge.client.ale.logicalreaders.ALELRServiceImpl;
import org.rifidi.edge.client.ale.logicalreaders.ALEService;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.rifidi.edge.client.ale.ecspecview";
	/** The ID of the preference that stores the Port URL */
	public static final String ALE_PORT_URL_PREF_NAME = PLUGIN_ID
			+ ".ALE_PORT_URL";
	/** The Default ALE port URL preference */
	public static final String ALE_PORT_URL_DEFAULT_ = "http://localhost:8081/ALEService";
	/** Logical reader preferences. */
	public final static String ALELR_ENDPOINT = "org.rifidi.edge.client.ale.logicalreaders.endpoint";
	public final static String ALELR_ENDPOINT_DEFAULT = "http://localhost:8081/ALELRService";
	/** Icons for the readers. */
	public static final String ICON_READER = "icon_reader";
	public static final String ICON_READER_LOCKED = "icon_reader_locked";
	public static final String ICON_SERVER = "icon_server";
	public static final String ICON_ECSPEC = "icon_ecspec";
	public static final String ICON_SUBSCRIBERS = "icon_subscribers";
	public static final String ICON_ECREPORT = "icon_ecreport";
	public static final String ICON_ECREPORTS = "icon_ecreports";
	public static final String ICON_ECREPORT_GROUP = "icon_ecreport_group";

	public static final String REPORT_RECEIVER_ADR = "127.0.0.1:10000";
	public static final String REPORT_RECEIVER_ADR_DEFAULT = "127.0.0.1:10000";
	// The shared instance
	private static Activator plugin;

	//TODO: temporary, move to spring ASAP
	private ALELRService alelrService = null;
	private ALEService aleService = null;
	
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
		alelrService = new ALELRServiceImpl();
		aleService=(ALEService)alelrService;
		plugin.getPreferenceStore().addPropertyChangeListener(alelrService);
	}

	/**
	 * TODO: Method level comment.  
	 * 
	 * @return
	 */
	public ALEService getAleService() {
		return aleService;
	}

	/**
	 * TODO: Method level comment.  
	 * 
	 * @return
	 */
	public ALELRService getAleLrService() {
		return alelrService;
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
		reg.put(ICON_READER, getImageDescriptor("icons/reader-16x16.png"));
		reg.put(ICON_READER_LOCKED,
				getImageDescriptor("icons/reader-lock.png"));
		reg.put(ICON_SERVER,
				getImageDescriptor("icons/server.png"));
		reg.put(ICON_ECSPEC,
				getImageDescriptor("icons/page.png"));
		reg.put(ICON_SUBSCRIBERS,
				getImageDescriptor("icons/email.png"));
		reg.put(ICON_ECREPORT,
				getImageDescriptor("icons/report.png"));
		reg.put(ICON_ECREPORTS,
				getImageDescriptor("icons/report_go.png"));
		reg.put(ICON_ECREPORT_GROUP,
				getImageDescriptor("icons/folder.png"));
	}
}
