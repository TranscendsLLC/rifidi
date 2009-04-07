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
	/** The time this event was generated */
	private long eventTimestamp;

	/**
	 * @param tags
	 * @param eventTimestamp
	 */
	public ReadCycle(Set<TagReadEvent> tags, long eventTimestamp) {
		this.tags = tags;
		this.eventTimestamp = eventTimestamp;
	}

	/**
	 * @return the tags
	 */
	public Set<TagReadEvent> getTags() {
		return Collections.unmodifiableSet(tags);
	}

	/**
	 * @return the eventTimestamp
	 */
	public long getEventTimestamp() {
		return eventTimestamp;
	}
}
