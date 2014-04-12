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

/**
 * This class represents a GPI Event on a sensor. Sensors can use this class
 * when an Input line changed.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class GPIEvent implements Serializable {

	/** The serial version ID for this class */
	private static final long serialVersionUID = 1L;
	/** The readerID which this GPI event happened on */
	private final String readerID;
	/** The port that changed */
	private final int port;
	/** if this port is high or low */
	private final boolean state;

	/**
	 * Constructor for a new GPIEvent
	 * 
	 * @param readerID
	 *            The reader which detected the Input event
	 * @param port
	 *            The port number for the event
	 * @param state
	 *            true if the port went high. False if it went low
	 */
	public GPIEvent(String readerID, int port, boolean state) {
		this.readerID = readerID;
		this.port = port;
		this.state = state;
	}

	/**
	 * The reader which detected the Input event
	 * 
	 * @return the readerID
	 */
	public String getReaderID() {
		return readerID;
	}

	/**
	 * The port that changed
	 * 
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * True if the port is high, false if it is low.
	 * 
	 * @return the state
	 */
	public boolean getState() {
		return state;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GPIEvent: " + readerID + " port " + port + " is "
				+ (state ? "High" : "Low");
	}

}
