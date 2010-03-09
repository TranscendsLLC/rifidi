/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.gpio.messages;

/**
 * @author kyle
 * 
 */
public abstract class AlienGPIOMessage {
	
	/** The data int which represents the current state of the external IO */
	private int data;

	/** The timestamp */
	private Long timestamp;

	/**
	 * @return the eventType
	 */
	public abstract GPIOEvent getEventType();

	/**
	 * @return the timestamp
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
	 * @return the data
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
