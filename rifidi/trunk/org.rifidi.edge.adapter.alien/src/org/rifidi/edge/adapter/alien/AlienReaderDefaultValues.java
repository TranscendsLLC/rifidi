/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
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
