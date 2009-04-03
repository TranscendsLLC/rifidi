/**
 * 
 */
package org.rifidi.edge.core.api.tags;

import java.io.Serializable;
import java.util.Set;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class TagBatch implements Serializable {

	/** SerialVersion ID */
	private static final long serialVersionUID = 1L;

	private String readerID;

	private long timestamp;

	private Set<TagDTO> tags;

	/**
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
	 * @return the readerID
	 */
	public String getReaderID() {
		return readerID;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @return the tags
	 */
	public Set<TagDTO> getTags() {
		return tags;
	}

}
