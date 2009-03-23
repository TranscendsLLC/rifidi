/**
 * 
 */
package org.rifidi.edge.client.sal.controller.edgeserver;

/**
 * An interface for controlling a RemoteEdgeServer
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface EdgeServerController {

	/**
	 * Update the edge server
	 */
	public void update();

	/**
	 * Connect to the edge server
	 */
	public void connect();

	/**
	 * Disconnect from the edge server
	 */
	public void disconnect();

	/**
	 * Save configuration on server
	 */
	public void saveConfiguration();

}
