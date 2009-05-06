/**
 * 
 */
package org.rifidi.edge.core.messages;
//TODO: Comments
import java.io.Serializable;

/**
 * This class acts as a wrapper around a DataContainerEvent (which stores tag
 * data). This class stores extra information about a tag read event, such as
 * the antenna that the tag was seen on
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class TagReadEvent implements Serializable {

	/** Serial Version ID for this class */
	private static final long serialVersionUID = 1L;
	/** The tag event */
	private DatacontainerEvent tag;
	/** The antenna the tag was seen on */
	private int antennaID;
	/** The time the tag was read */
	private long timestamp;
	/** The ID of the reader that saw the tags */
	private String readerID;

	/**
	 * @param tag
	 * @param antennaID
	 * @param timestamp
	 */
	public TagReadEvent(String readerID, DatacontainerEvent tag, int antennaID,
			long timestamp) {
		this.tag = tag;
		this.antennaID = antennaID;
		this.timestamp = timestamp;
		this.readerID = readerID;
	}

	/**
	 * @return the readerID
	 */
	public String getReaderID() {
		return readerID;
	}

	/**
	 * @return the tag
	 */
	public DatacontainerEvent getTag() {
		return tag;
	}

	/**
	 * @return the antennaID
	 */
	public int getAntennaID() {
		return antennaID;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

}
