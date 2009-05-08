/*
 *  LLRPConstants.java
 *
 *  Created:	Mar 9, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.llrp;

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
	public final static String RECONNECTION_INTERVAL = "5001";
	
	/**
	 * The max times to try to connect before giving up.  
	 */
	public final static String MAX_CONNECTION_ATTEMPTS = "10";
}
