package org.rifidi.edge.notification;

import java.util.Date;

/**
 * 
 * @author Matthew Dean - matt@transcends.co
 */
public class SensorConnectingEvent extends SensorStatusEvent {
	
	private static final long serialVersionUID = 6282486074505373195L;
	/** The ID of the session that was disconnected */
	private final String sessionID;
	/** The string to display in the toString() method */
	private final String toString;

	/**
	 * Constructor
	 * 
	 * @param sensorID
	 *            The ID of the sensor became disconnected
	 * @param timestamp
	 *            The timestamp of the event
	 * @param sessionID
	 *            The ID of the session that became disconnected.
	 */
	public SensorConnectingEvent(String sensorID, Long timestamp,
			String sessionID) {
		super(sensorID, timestamp);
		this.sessionID = sessionID;
		toString = "SENSOR DISCONNECTED EVENT: " + sensorID + ":" + sessionID
				+ " at " + dateFormat.format(new Date(timestamp));
	}

	/**
	 * @return the ID of the session that became disconnected.
	 */
	public String getSessionID() {
		return sessionID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return toString;
	}
}
