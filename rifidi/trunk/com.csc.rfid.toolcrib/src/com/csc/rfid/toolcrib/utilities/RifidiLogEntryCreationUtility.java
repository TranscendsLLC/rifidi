/*
 *  RifidiLogitemCreationUtility.java
 *
 *  Created:	Mar 5, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package com.csc.rfid.toolcrib.utilities;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Set;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class RifidiLogEntryCreationUtility {

	/**
	 * 
	 */
	public RifidiLogEntryCreationUtility() {
	}

	public static final String CONSTANT_1 = "RFID-CAL-LAB";

	public static final String OUTBOUND = "-OUT";

	public static final String INBOUND = "-IN";

	public static final String INBOUND_SPACES = "               ";

	public static final String OUTBOUND_SPACES = "              ";

	// *********************SAP LOG BELOW HERE***************************

	/**
	 * Creates an SAP log entry. It will tell which tags were seen by the
	 * readers and if they were inbound or outbound.
	 * 
	 * @param tag
	 * @param inbound
	 * @return
	 */
	public String createSAPEntry(String tag, boolean inbound) {
		StringBuilder sb = new StringBuilder();

		if (tag.length() >= 14) {
			tag = tag.substring(2, 14);
		} else {
			return "";
		}
		tag = tag.toUpperCase();
		if (inbound) {
			sb.append(tag + CONSTANT_1 + INBOUND + INBOUND_SPACES
					+ createDate());
		} else {
			sb.append(tag + CONSTANT_1 + OUTBOUND + OUTBOUND_SPACES
					+ createDate());
		}
		sb.append(NEWLINE);
		return sb.toString();
	}

	/*
	 * Returns the current time as a date in yyyyMMddHHmmss format.
	 */
	private static String createDate() {
		return createDate(System.currentTimeMillis());
	}

	/**
	 * 
	 * @param timestamp
	 *            The timestamp
	 * @return
	 */
	private static String createDate(long timestamp) {
		Date now = new Date(timestamp);
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		return format.format(now);
	}

	// *******************STANDARD LOG BELOW HERE***************************

	public static final String COMMA = ",";

	public static final String WATCH_LIST_TOKEN = "1";

	private static final String NEWLINE = "\r\n";

	/**
	 * Creates a standard log entry. This will go in the standard log.
	 * 
	 * @param epc
	 * @param readerID
	 * @param inbound
	 * @param onWatchList
	 * @return
	 */
	public String createStandardLogEntry(String epc, String readerID,
			boolean inbound, boolean onWatchList) {
		StringBuilder sb = new StringBuilder();

		sb.append(epc);
		sb.append(COMMA);
		sb.append(getDirection(inbound));
		sb.append(COMMA);
		sb.append(getWDPValue(readerID));
		sb.append(COMMA);
		sb.append(createDate());
		if (onWatchList) {
			sb.append(COMMA);
			sb.append("1");
		}
		sb.append(NEWLINE);

		return sb.toString();
	}

	/*
	 * This method returns either "W" for window, "D" for door, or "P" for
	 * portal based on which reader the tag was seen on.
	 * 
	 * @return
	 */
	private static String getWDPValue(String readerID) {
		if (readerID.equalsIgnoreCase(System
				.getProperty("com.csc.window_reader"))) {
			return System.getProperty("com.csc.window_reader_id");
		} else if (readerID.equalsIgnoreCase(System
				.getProperty("com.csc.door_reader"))) {
			return System.getProperty("com.csc.door_reader_id");
		} else if (readerID.equalsIgnoreCase(System
				.getProperty("com.csc.portal_reader"))) {
			return System.getProperty("com.csc.portal_reader_id");
		}
		// TODO: If it gets this far, something is wrong. Log it?
		return "ERROR";
	}

	// Outbound
	private static String OUT = "OUT";
	// Inbound
	private static String IN = "IN";

	/*
	 * This method returns either "IN" or "OUT"
	 */
	private static String getDirection(boolean inbound) {
		if (inbound) {
			return IN;
		} else {
			return OUT;
		}
	}

	// *******************GHOST LOG BELOW HERE***************************

	/**
	 * Creates a log if there is a ghost read of a tag on a particular reader.
	 * No set format for this log entry as of yet.
	 */
	public String createGhostReadLogEntry(String epc, Set<String> set) {
		StringBuilder sb = new StringBuilder();

		sb.append("Ghost read of tag " + epc + " at locations: ");

		for (String r : set) {
			sb.append(getWDPValue(r));
			sb.append(COMMA);
		}
		sb.append(NEWLINE);

		return sb.toString();
	}

	// *******************DOWNTIME LOG BELOW HERE***************************

	/**
	 * This method logs downtime for a particular reader. It takes in the ID of
	 * the reader and generates the current time as a date for when the reader
	 * went down.
	 */
	public static String createDowntimeLogEntry(String readerID, long timestamp) {
		StringBuilder sb = new StringBuilder();

		sb.append("Reader " + getWDPValue(readerID) + " went down at "
				+ createDate(timestamp));

		return sb.toString();
	}

	/**
	 * This method logs downtime for a particular reader. It takes in the ID of
	 * the reader and generates the current time as a date for when the reader
	 * went down.
	 */
	public static String createUptimeLogEntry(String readerID, long timestamp) {
		StringBuilder sb = new StringBuilder();

		sb.append("Reader " + getWDPValue(readerID) + " went up at "
				+ createDate(timestamp));

		return sb.toString();
	}
}
