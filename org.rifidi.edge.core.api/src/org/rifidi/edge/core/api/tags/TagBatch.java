package org.rifidi.edge.core.api.tags;

import java.io.Serializable;
import java.util.Set;

/**
 * A Wrapper object around Tag Events for sending to a client
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class TagBatch implements Serializable {

	/** SerialVersion ID */
	private static final long serialVersionUID = 1L;

	private String readerID;

	private long timestamp;

	private Set<TagDTO> tags;

	/**
	 * Constructor.
	 * 
	 * @param readerID
	 * @param timestamp
	 * @param tags
	 */
	public TagBatch(String readerID, long timestamp, Set<TagDTO> tags) {
		this.readerID = readerID;
		this.timestamp = timestamp;
		this.tags = tags;
	}

	/**
	 * Returns the ID of the reader.
	 * 
	 * @return the readerID
	 */
	public String getReaderID() {
		return readerID;
	}

	/**
	 * Returns the timestamp for this batch.
	 * 
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * Returns the set of tags.
	 * 
	 * @return the tags
	 */
	public Set<TagDTO> getTags() {
		return tags;
	}

}
