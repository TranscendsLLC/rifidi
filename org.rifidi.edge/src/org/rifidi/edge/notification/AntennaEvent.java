/**
 * 
 */
package org.rifidi.edge.notification;

import java.io.Serializable;
import java.text.SimpleDateFormat;

/**
 * @author Matthew Dean - matt@transcends.co
 *
 */
public class AntennaEvent implements Serializable {
	
	private static final long serialVersionUID = 1L;
	/** The ID of the sensor */
	private final String sensorID;
	/** The ID of the antenna */
	private final Integer antennaID;
	/** The timestamp that went down */
	private final Long timestamp;
	/** Is the antenna going up or coming down */
	private final Boolean up;
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
	public AntennaEvent(String readerID, Integer antennaID, Long timestamp, Boolean up) {
		super();
		this.sensorID = readerID;
		this.timestamp = timestamp;
		this.antennaID = antennaID;
		this.up = up;
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
	
	/**
	 * @return The ID of the antenna
	 */
	public Integer getAntennaID() {
		return antennaID;
	}
	
	/**
	 * @return Whether the antenna is going up or coming down
	 */
	public Boolean isUp() {
		return up;
	}
}

