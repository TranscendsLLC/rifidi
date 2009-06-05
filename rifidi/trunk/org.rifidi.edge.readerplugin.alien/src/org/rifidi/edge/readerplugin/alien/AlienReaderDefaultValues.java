/**
 * 
 */
package org.rifidi.edge.readerplugin.alien;

/**
 * A class that contains static default values for certain properties of an
 * Alien9800 reader
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AlienReaderDefaultValues {

	/** IP address of the readerSession. */
	public static final String IPADDRESS = "127.0.0.1";
	/** Port to connect to. */
	public static final String PORT = "20000";
	/** Username for the telnet interface. */
	public static final String USERNAME = "alien";
	/** Password for the telnet interface. */
	public static final String PASSWORD = "password";
	/** Time between two connection attempts. */
	public static final String RECONNECTION_INTERVAL = "500";
	/** Number of connection attempts before a connection goes into fail state. */
	public static final String MAX_CONNECTION_ATTEMPTS = "10";

}
