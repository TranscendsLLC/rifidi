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
package org.rifidi.edge.notification;

import java.util.Date;


/**
 * This class represents an event when a sensor becomes connected.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * @author Matthew Dean - matt@transcends.co
 */
public class SensorProcessingEvent extends SensorStatusEvent {

	private static final long serialVersionUID = 7549924222357637889L;
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
	public SensorProcessingEvent(String sensorID, Long timestamp,
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
