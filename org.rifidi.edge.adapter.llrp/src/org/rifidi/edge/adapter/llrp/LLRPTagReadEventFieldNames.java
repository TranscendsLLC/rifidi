/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
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
