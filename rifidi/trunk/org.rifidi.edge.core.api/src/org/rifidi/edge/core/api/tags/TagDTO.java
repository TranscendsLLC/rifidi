/**
 * 
 */
package org.rifidi.edge.core.api.tags;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class TagDTO implements Serializable {

	/** SerialVersionID */
	private static final long serialVersionUID = 1L;

	private BigInteger tagID;

	private int antennaNumber;

	private long timestamp;

	/**
	 * @param tagID
	 * @param antennaNumber
	 * @param timestamp
	 */
	public TagDTO(BigInteger tagID, int antennaNumber, long timestamp) {
		this.tagID = tagID;
		this.antennaNumber = antennaNumber;
		this.timestamp = timestamp;
	}

	/**
	 * @return the tagID
	 */
	public BigInteger getTagID() {
		return tagID;
	}

	/**
	 * @return the antennaNumber
	 */
	public int getAntennaNumber() {
		return antennaNumber;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

}
