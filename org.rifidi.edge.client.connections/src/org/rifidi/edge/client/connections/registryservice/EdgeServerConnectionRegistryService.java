package org.rifidi.edge.client.connections.registryservice;

import java.util.Set;

import org.rifidi.edge.client.connections.edgeserver.EdgeServerConnection;
import org.rifidi.edge.client.connections.exceptions.EdgeServerConnectionRegistryException;
import org.rifidi.edge.client.connections.registryservice.listeners.EdgeServerConnectionRegistryListener;

/**
 * This interface is a Registry for Edge Server connections All edge server
 * connections are identified by an ID that is assigned to it when the
 * connection is created
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface EdgeServerConnectionRegistryService {

	/**
	 * Create the EdgeServerConnection and store in the regsitry
	 * 
	 * @param ip
	 *            The IP of the Edge Server
	 * @param port
	 *            The RMI port for the Edge Server
	 * @param jmsPort
	 * @return An ID that identifies this edge server to the registry
	 * @throws EdgeServerConnectionRegistryException
	 */
	public int createConnection(String ip, int port, int jmsPort)
			throws EdgeServerConnectionRegistryException;

	/**
	 * Destroy the connection to the edge server and remove it from the registry
	 * 
	 * @param id
	 */
	public void destroyConnection(int id);

	/**
	 * Return the EdgeServerConnection for the given id. returns null if there
	 * is no entry for the id
	 * 
	 * @param id
	 * @return
	 */
	public EdgeServerConnection getConnection(int id);

	/**
	 * Get a list of the ids for all the connections
	 * 
	 * @return
	 */
	public Set<Integer> getConnectionIDs();

	public abstract void addListener(
			EdgeServerConnectionRegistryListener listener);

	public abstract void removeListener(
			EdgeServerConnectionRegistryListener listener);

}