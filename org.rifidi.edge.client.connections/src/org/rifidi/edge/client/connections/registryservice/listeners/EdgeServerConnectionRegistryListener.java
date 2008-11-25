package org.rifidi.edge.client.connections.registryservice.listeners;

import org.rifidi.edge.client.connections.edgeserver.EdgeServerConnection;

/**
 * A listener to add and remove events from the EdgeServerConnectionRegistry
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface EdgeServerConnectionRegistryListener {

	/**
	 * Event fired when a server has been added to the registry
	 * 
	 * @param server
	 *            the server that was added
	 */
	public void serverAdded(EdgeServerConnection server);

	/**
	 * Event fired when a server was removed from the registry
	 * 
	 * @param server
	 *            the server that was removed
	 */
	public void serverRemoved(EdgeServerConnection server);
}
