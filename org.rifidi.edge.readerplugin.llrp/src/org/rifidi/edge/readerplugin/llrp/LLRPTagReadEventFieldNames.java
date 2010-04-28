/*
 *  LLRPTagReadEventFieldNames.java
 *
 *  Created:	Apr 28, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.llrp;

/**
 * @author Matthew Dean - matt@pramari.com
 *
 */
public interface LLRPTagReadEventFieldNames {
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
