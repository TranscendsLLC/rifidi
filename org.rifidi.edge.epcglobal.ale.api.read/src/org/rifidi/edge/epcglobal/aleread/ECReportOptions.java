package org.rifidi.edge.epcglobal.aleread;

/**
 * Options that descirbe what has to go into a report being sent back to client.
 * OR together.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class ECReportOptions {
	/** Include the count of tags in the report. */
	public static final int INCLUDE_COUNT = 1;
	/** Include the EPC field. */
	public static final int INCLUDE_EPC = 2;
	/** Include the raw decimal representation. */
	public static final int INCLUDE_RAW_DECIMAL = 4;
	/** Include the raw hex representation. */
	public static final int INCLUDE_RAW_HEX = 8;
	/** Include the decoded tag representation. */
	public static final int INCLUDE_TAG = 16;
	/** Include the spec that generated the report. */
	public static final int INCLUDE_SPEC_IN_REPORTS = 32;
	/** Send a report even if it is empty. */
	public static final int REPORT_IF_EMPTY = 64;
	/** Only send a report if there was a change to the set of tags. */
	public static final int REPORT_ONLY_ON_CHANGE = 128;
	/** Only send tags that were not in the last report. */
	public static final int REPORT_ADDITIONS = 256;
	/** Only send tags that disappeared since the last report. */
	public static final int REPORT_DELETIONS = 512;
	/** Report the set currently visible tags. */
	public static final int REPORT_CURRENT = 1024;
}
