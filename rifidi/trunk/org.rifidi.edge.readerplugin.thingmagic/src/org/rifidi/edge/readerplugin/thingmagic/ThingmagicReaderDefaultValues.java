/*
 *  ThingmagicReaderDefaultValues.java
 *
 *  Created:	Sep 29, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.thingmagic;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class ThingmagicReaderDefaultValues {
	/** IP address of the sensorSession. */
	public static final String IPADDRESS = "127.0.0.1";
	/** Port to connect to. */
	public static final String PORT = "8080";
	/** Time between two connection attempts. */
	public static final String RECONNECTION_INTERVAL = "500";
	/** Number of connection attempts before a connection goes into fail state. */
	public static final String MAX_CONNECTION_ATTEMPTS = "10";
}
