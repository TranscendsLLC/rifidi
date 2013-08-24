/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.llrp;

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
