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
	public final static String RECONNECTION_INTERVAL = "500";

	/**
	 * The max times to try to connect before giving up.
	 */
	public final static String MAX_CONNECTION_ATTEMPTS = "-1";

	/**
	 * The label for an RSSI value in the extra information in a TagReadeEvent
	 */
	public final static String RSSI_ID = "RSSI";

	/**
	 * The label for an ROSPEC value in the extra information in a TagReadeEvent
	 */
	public final static String ROSPEC_ID = "ROSPEC";

	/**
	 * The label for an SPECINDEX value in the extra information in a
	 * TagReadeEvent
	 */
	public final static String SPEC_INDEX = "SPECINDEX";

	/**
	 * The label for an INVENTORY_PARAMETER_SPEC_ID value in the extra
	 * information in a TagReadeEvent
	 */
	public final static String INVPARAMSPECID = "INVPARAMSPECID";
	
	
	/**
	 * The label for an CHANNEL INDEX value in the extra information in a TagReadeEvent
	 */
	public final static String CHANNELINDEX = "CHANNELINDEX";
	
	/**
	 * The label for an FIRST_SEEN_TIMESTAMP_UTC value in the extra information in a TagReadeEvent
	 */
	public final static String FIRSTSEENUTC = "FIRSTSEENUTC";
	
	/**
	 * The label for an FIRST_SEEN_TIMESTAMP_UPTIME value in the extra information in a TagReadeEvent
	 */
	public final static String FIRSTSEENUPTIME = "FIRSTSEENUPTIME";

	/**
	 * The label for an LAST_SEEN_TIMESTAMP_UTC value in the extra information in a TagReadeEvent
	 */
	public final static String LASTSEENUTC = "LASTSEENUTC";
	
	/**
	 * The label for an LAST_SEEN_TIMESTAMP_UPTIME value in the extra information in a TagReadeEvent
	 */
	public final static String LASTSEENUPTIME = "LASTSEENUPTIME";
	
	/**
	 * The label for an TAG SEEN COUNT value in the extra information in a TagReadeEvent
	 */
	public final static String TAGSEENCOUNT = "TAGSEENCOUNT";
	
	/**
	 * The label for an AIR_PROTOCOL_TAG_DATA CRC value in the extra information in a TagReadeEvent
	 */
	public final static String AIRPROT_CRC = "AIRPROT_CRC";
	
	/**
	 * The label for an AIR_PROTOCOL_TAG_DATA PC value in the extra information in a TagReadeEvent
	 */
	public final static String AIRPROT_PC = "AIRPROT_PC";
}
