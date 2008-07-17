package org.rifidi.edge.core.communication.service;

import org.rifidi.edge.core.communication.Connection;

/**
 * ConnectionService listener to monitor the creation and the removal of
 * connections
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public interface ConnectionServiceListener {

	/**
	 * New Connection created event
	 * 
	 * @param connection the connection created
	 */
	public void addEvent(Connection connection);

	/**
	 * Connection removed event
	 * 
	 * @param connection the connection removed
	 */
	public void removeEvent(Connection connection);

}
