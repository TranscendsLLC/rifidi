package org.rifidi.edge.core.services.notification.data.gpio;

/**
 * This class represents a GPO Event on a sensor. Sensors can use this class
 * when an Output line changed.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class GPOEvent {

	/** The serial version ID for this class */
	private static final long serialVersionUID = 1L;
	/** The readerID which this GPO event happened on */
	private final String readerID;
	/** The port that changed */
	private final int port;
	/** if this port is high or low */
	private final boolean state;

	/**
	 * Constructor for a new GPOEvent
	 * 
	 * @param readerID
	 *            The reader which detected the Output event
	 * @param port
	 *            The port number for the event
	 * @param state
	 *            true if the port went high. False if it went low
	 */
	public GPOEvent(String readerID, int port, boolean state) {
		this.readerID = readerID;
		this.port = port;
		this.state = state;
	}

	/**
	 * The reader which detected the Output event
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
		return "GPOEvent: " + readerID + " port " + port + " is "
				+ (state ? "High" : "Low");
	}

}
