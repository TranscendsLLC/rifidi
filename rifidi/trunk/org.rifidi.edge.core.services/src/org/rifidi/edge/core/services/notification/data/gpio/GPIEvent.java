/**
 * 
 */
package org.rifidi.edge.core.services.notification.data.gpio;

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
