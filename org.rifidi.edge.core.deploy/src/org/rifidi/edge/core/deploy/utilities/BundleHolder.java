package org.rifidi.edge.core.deploy.utilities;

import java.util.List;

import org.osgi.framework.Bundle;
import org.rifidi.edge.core.readerplugin.ReaderPlugin;
import org.rifidi.edge.core.readerplugin.xml.CommandDescription;

/**
 * Bundle helper containing all necessary information to unload the Fragment
 * later on
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class BundleHolder {

	/**
	 * Host Bundle of the Fragment
	 */
	public Bundle host;

	/**
	 * Fragment Bundle contributing commands to the Host
	 */
	public Bundle fragment;

	/**
	 * Commands this fragment provides
	 */
	public List<CommandDescription> commands;
	
	/**
	 * Commands this fragment provides
	 */
	public List<CommandDescription> properties;

	/**
	 * ReaderPlugin the commands where added to
	 */
	public ReaderPlugin plugin;

	/**
	 * Constructor for convenience
	 * 
	 * @param host
	 *            HostBundle associated with the Fragment
	 * @param fragment
	 *            FragmentBundle
	 */
	public BundleHolder(Bundle host, Bundle fragment) {
		this.host = host;
		this.fragment = fragment;
	}

}
