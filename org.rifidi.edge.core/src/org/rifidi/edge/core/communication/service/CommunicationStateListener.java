package org.rifidi.edge.core.communication.service;

/**
 * Listener for communication events
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public interface CommunicationStateListener {

	/**
	 * Communication connected event
	 */
	public void conn_connected();

	/**
	 * Communication disconnected event
	 */
	public void conn_disconnected();

	/**
	 * Communication not recoverable event
	 */
	public void conn_error();

}
