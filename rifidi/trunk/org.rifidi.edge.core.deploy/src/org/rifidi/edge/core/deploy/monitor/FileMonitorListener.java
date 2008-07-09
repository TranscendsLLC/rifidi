package org.rifidi.edge.core.deploy.monitor;

import java.io.File;

/**
 * Listener for file system changes for a FileMonitor
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public interface FileMonitorListener {

	/**
	 * New File event
	 * 
	 * @param f
	 *            new file found
	 */
	public void fileAddedEvent(File f);

	/**
	 * File removed event
	 * 
	 * @param f
	 *            file removed
	 */
	public void fileRemovedEvent(File f);
}
