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

import java.io.Serializable;
import java.text.SimpleDateFormat;

/**
 * This is an abstract class for Sensor Status Events (such as when a session is
 * connected or disconnected) to implement. These objects are submitted to
 * esper.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class SensorStatusEvent implements Serializable {
	/** the serial Version ID */
	private static final long serialVersionUID = 1L;
	/** The ID of the sensor */
	private final String sensorID;
	/** The timestamp that went down */
	private final Long timestamp;
	/** A date format used in the toString method */
	protected static SimpleDateFormat dateFormat;
	static {
		dateFormat = new SimpleDateFormat("yyyyy.MMMMM.dd HH:mm:ss");
	}

	/**
	 * Constructor
	 * 
	 * @param sensorID
	 *            The ID of the sensor for this event.
	 * @param timestamp
	 *            The timestamp of when this event happened.
	 */
	public SensorStatusEvent(String readerID, Long timestamp) {
		super();
		this.sensorID = readerID;
		this.timestamp = timestamp;
	}

	/**
	 * @return the ID of the Sensor for this event
	 */
	public String getSensorID() {
		return sensorID;
	}

	/**
	 * @return the time this event happened.
	 */
	public Long getTimestamp() {
		return timestamp;
	}

}
