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
	public void connected();

	/**
	 * Communication disconnected event
	 */
	public void disconnected();

	/**
	 * Communication not recoverable event
	 */
	public void error();

}
