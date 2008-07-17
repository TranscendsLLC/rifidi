package org.rifidi.edge.core.deploy.service;

import java.util.List;

/**
 * This is the Interface for the methods a Deploy Service needs to provide
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public interface DeployService {

	/**
	 * Add a new list of Directories to monitor
	 * 
	 * @param directoriesToMonitor
	 *            list of Strings representing the directory names
	 */
	public void add(List<String> directoriesToMonitor);

	/**
	 * Remove a list of Directories to monitor
	 * 
	 * @param directories
	 *            list of Strings representing the directory names
	 */
	public void remove(List<String> directories);

}
