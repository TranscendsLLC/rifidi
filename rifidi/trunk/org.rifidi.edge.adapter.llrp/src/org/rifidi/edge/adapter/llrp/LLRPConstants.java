/*
 * 
 * LLRPConstants.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
package org.rifidi.edge.adapter.llrp;

/**
 * Constants class for the LLRP plugin.
 * 
 * @author Matthew Dean
 */
public class LLRPConstants {
	/**
	 * Default IP for the reader.
	 */
	public final static String LOCALHOST = "127.0.0.1";

	/**
	 * Default port for the reader.
	 */
	public final static String PORT = "5084";

	/**
	 * Default port for the reader.
	 */
	public final static String PORT_MAX = "65535";

	/**
	 * Default port for the reader.
	 */
	public final static String PORT_MIN = "0";

	/**
	 * THe interval between reconnect attempts.
	 */
	public final static String RECONNECTION_INTERVAL = "500";

	/**
	 * The max times to try to connect before giving up.
	 */
	public final static String MAX_CONNECTION_ATTEMPTS = "-1";

	/** The default path to the SET_READER_CONFIG XML */
	public final static String SET_READER_CONFIG_PATH = "sensorconfig/SET_READER_CONFIG.llrp";

}
