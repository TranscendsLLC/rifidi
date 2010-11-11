/*
 * 
 * AlienReaderDefaultValues.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
/**
 * 
 */
package org.rifidi.edge.adapter.alien;

/**
 * A class that contains static default values for certain properties of an
 * Alien9800 reader
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AlienReaderDefaultValues {

	/** IP address of the sensorSession. */
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
	public static final String MAX_CONNECTION_ATTEMPTS = "-1";

}
