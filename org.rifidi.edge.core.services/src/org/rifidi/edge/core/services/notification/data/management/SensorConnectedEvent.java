package org.rifidi.edge.core.services.notification.data.management;

import java.util.Date;

/**
 * This class represents an event when a sensor becomes connected.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class SensorConnectedEvent extends SensorStatusEvent {

	/** the serial Version ID */
	private static final long serialVersionUID = 1L;
	/** The ID of the session that went up */
	private final String sessionID;
	/** The string to display in toString() */
	private final String toString;

	/**
	 * Constructor
	 * 
	 * @param sensorID
	 *            The sensor that was connected
	 * @param timestamp
	 *            The time the sensor was connected
	 * @param sessionID
	 *            The session of the sensor that was connected.
	 */
	public SensorConnectedEvent(String sensorID, Long timestamp,
			String sessionID) {
		super(sensorID, timestamp);
		this.sessionID = sessionID;
		this.toString = "SENSOR CONNECTED EVENT: " + sensorID + ":" + sessionID
				+ " at " + dateFormat.format(new Date(timestamp));
	}

	/**
	 * @return the ID of the session that was connected.
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
