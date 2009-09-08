/*
 * 
 * ReadCycle.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
package org.rifidi.edge.core.services.notification.data;

import java.io.Serializable;
import java.util.Set;

/**
 * This represents a read cycle from a reader.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class ReadCycle implements Serializable {

	/** The serial version ID for this class */
	private static final long serialVersionUID = 1L;
	private final TagReadEvent[] tags;
	/** The time this event was generated */
	private long eventTimestamp;
	private String readerID;

	/**
	 * Constructor.
	 * 
	 * @param tags
	 * @param eventTimestamp
	 */
	public ReadCycle(Set<TagReadEvent> tags, String readerID,
			long eventTimestamp) {
		this.tags = tags.toArray(new TagReadEvent[0]);
		this.eventTimestamp = eventTimestamp;
		this.readerID = readerID;
	}

	/**
	 * Returns the tags
	 * 
	 * @return
	 */
	public TagReadEvent[] getTags() {
		return tags;
	}

	/**
	 * Returns the timestamp for this event.
	 * 
	 * @return the eventTimestamp
	 */
	public long getEventTimestamp() {
		return eventTimestamp;
	}

	/**
	 * Returns the ID for this reader.
	 * 
	 * @return The ID of the reader this was seen on
	 */
	public String getReaderID() {
		return this.readerID;
	}
}
