/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.notification;

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
