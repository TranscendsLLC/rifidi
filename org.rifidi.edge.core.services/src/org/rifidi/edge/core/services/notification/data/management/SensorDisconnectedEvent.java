package org.rifidi.edge.core.services.notification.data.management;

import java.util.Date;

/**
 * This class represents an event when a sensor becomes disconnected.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class SensorDisconnectedEvent extends SensorStatusEvent {

	/** Serial Version UID */
	private static final long serialVersionUID = 1L;
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
	public SensorDisconnectedEvent(String sensorID, Long timestamp,
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
