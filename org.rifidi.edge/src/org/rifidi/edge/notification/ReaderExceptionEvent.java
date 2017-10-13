/**
 * 
 */
package org.rifidi.edge.notification;

import java.io.Serializable;

/**
 * An exception generated from a reader
 * 
 * @author Matthew Dean - matt@transcends.co
 */
public class ReaderExceptionEvent implements Serializable {
	
	private static final long serialVersionUID = -7517570085860003227L;
	/** The ID of the sensor */
	private final String sensorID;
	/** The timestamp that went down */
	private final Long timestamp;
	/** A description of the error */
	private final String errordesc;
	/** The status code of the error */
	private final String statuscode;
	
	/**
	 * Constructor
	 * 
	 * @param sensorID
	 *            The ID of the sensor for this event.
	 * @param timestamp
	 *            The timestamp of when this event happened.
	 */
	public ReaderExceptionEvent(String readerID, Long timestamp, String errordesc, String statuscode) {
		this.sensorID = readerID;
		this.timestamp = timestamp;
		this.errordesc = errordesc;
		this.statuscode = statuscode;
	}

	public String getSensorID() {
		return sensorID;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public String getErrordesc() {
		return errordesc;
	}

	public String getStatuscode() {
		return statuscode;
	}
	
	
}
