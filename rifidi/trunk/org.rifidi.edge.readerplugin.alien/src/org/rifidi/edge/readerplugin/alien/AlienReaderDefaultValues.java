/**
 * 
 */
package org.rifidi.edge.readerplugin.alien;
//TODO: Comments
/**
 * @author kyle
 *
 */
public class AlienReaderDefaultValues {
	
	/** IP address of the readerSession. */
	public static final String ipAddress = "127.0.0.1";
	/** Port to connect to. */
	public static final String port = "20000";
	/** Username for the telnet interface. */
	public static final String username = "alien";
	/** Password for the telnet interface. */
	public static final String password = "password";
	/** Time between two connection attempts. */
	public static final String reconnectionInterval = "500";
	/** Number of connection attempts before a connection goes into fail state. */
	public static final String maxNumConnectionAttempts = "10";

}
