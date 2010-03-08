package org.rifidi.edge.core.services.notification.data.management;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class represents an event when a Session goes up. It is given to esper.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class SessionUpEvent {

	/** the serial Version ID */
	private static final long serialVersionUID = 1L;
	/** The ID of the reader */
	private String readerID;
	/** The ID of the session that went up */
	private String sessionID;
	/** The timestamp that went up */
	private Long timestamp;
	/** A date format used in the toString method */
	private static SimpleDateFormat dateFormat;
	static {
		dateFormat = new SimpleDateFormat("yyyyy.MMMMM.dd HH:mm:ss");
	}

	/**
	 * @return the readerID
	 */
	public String getReaderID() {
		return readerID;
	}

	/**
	 * @param readerID
	 *            the readerID to set
	 */
	public void setReaderID(String readerID) {
		this.readerID = readerID;
	}

	/**
	 * @return the sessionID
	 */
	public String getSessionID() {
		return sessionID;
	}

	/**
	 * @param sessionID
	 *            the sessionID to set
	 */
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	/**
	 * @return the timestamp
	 */
	public Long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SESSION UP EVENT: " + readerID + ":" + sessionID + " at "
				+ dateFormat.format(new Date(timestamp));
	}

}
