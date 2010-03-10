/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.gpio.messages;

/**
 * This is an abstract class that represents a message received from the
 * IOStream from an Alien reader.
 * 
 * @author Kyle Neumeier - kyle@pramari.com @ *
 */
public abstract class AlienGPIOMessage {

	/** The data int which represents the current state of the external IO */
	private int data;

	/** The timestamp */
	private Long timestamp;

	/**
	 * @return the type of IOEvent message received from the alien reader
	 */
	public abstract GPIOEvent getEventType();

	/**
	 * @return the timestamp that the event was received at
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

	/**
	 * @return the data byte
	 */
	public int getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(int data) {
		this.data = data;
	}

}
