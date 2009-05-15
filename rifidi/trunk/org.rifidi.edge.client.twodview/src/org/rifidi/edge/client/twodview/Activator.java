package org.rifidi.edge.client.twodview;

//TODO: Comments
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.rifidi.edge.client.twodview";

	// The shared instance
	private static Activator plugin;

	public static final String IMG_READER_UNKNOWN = "readerunknown";
	public static final String IMG_READER_ON = "readeron";
	public static final String IMG_READER_OFF = "readeroff";
	public static final String IMG_READER_CONNECTING = "readerconnecting";
	public static final String IMG_READER_WORKING = "readeroworking";

	/**
	 * The folder for all scenes
	 */
	private static IFolder floorplanFolder;
	private static IFolder saveFolder;

	/**
	 * @return the floorplanFolder
	 */
	public static IFolder getFloorplanFolder() {
		return floorplanFolder;
	}

	/**
	 * @return the saveFolder
	 */
	public static IFolder getSaveFolder() {
		return saveFolder;
	}

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
		// check workspace for required folders
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject project = root.getProject("MyProject");
		if (!project.exists()) {
			project.create(null);
		}
		project.open(null);
		floorplanFolder = project.getFolder("Floorplans");
		if (!floorplanFolder.exists()) {
			floorplanFolder.create(true, true, null);
		}
		saveFolder = project.getFolder("SaveFiles");
		if (!saveFolder.exists()) {
			saveFolder.create(true, true, null);
		}

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
		reg.put(IMG_READER_UNKNOWN,
				getImageDescriptor("icons/reader-unknown.png"));
		reg
				.put(IMG_READER_ON,
						getImageDescriptor("icons/reader-connected.png"));
		reg.put(IMG_READER_OFF,
				getImageDescriptor("icons/reader-disconnected.png"));
		reg.put(IMG_READER_CONNECTING,
				getImageDescriptor("icons/reader-connecting.png"));
		reg.put(IMG_READER_WORKING,
				getImageDescriptor("icons/reader-working.png"));
	}

}
