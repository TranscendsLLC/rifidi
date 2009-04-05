/**
 * 
 */
package org.rifidi.edge.core.messages;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

/**
 * This represents a read cycle from a reader.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ReadCycle implements Serializable {

	/** The serial version ID for this class */
	private static final long serialVersionUID = 1L;
	/** The set of tags seen */
	private Set<TagReadEvent> tags;
	/** The ID of the reader that saw the tags */
	private String readerID;
	/** The time this event was generated */
	private long eventTimestamp;

	/**
	 * @param tags
	 * @param readerID
	 * @param eventTimestamp
	 */
	public ReadCycle(Set<TagReadEvent> tags, String readerID,
			long eventTimestamp) {
		this.tags = tags;
		this.readerID = readerID;
		this.eventTimestamp = eventTimestamp;
	}

	/**
	 * @return the tags
	 */
	public Set<TagReadEvent> getTags() {
		return Collections.unmodifiableSet(tags);
	}

	/**
	 * @return the readerID
	 */
	public String getReaderID() {
		return readerID;
	}

	/**
	 * @return the eventTimestamp
	 */
	public long getEventTimestamp() {
		return eventTimestamp;
	}
}
