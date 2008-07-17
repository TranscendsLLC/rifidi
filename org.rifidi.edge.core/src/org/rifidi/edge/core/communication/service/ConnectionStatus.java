package org.rifidi.edge.core.communication.service;

/**
 * Status of a specific Connection
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public enum ConnectionStatus {

	/**
	 * The Connection is connected
	 */
	CONNECTED,
	/**
	 * The Connection is disconnected
	 */
	DISCONNECTED,
	/**
	 * The Connection is not valid anymore
	 */
	ERROR
}
