/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
package org.rifidi.edge.notification;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.rifidi.edge.api.TagBatch;
import org.rifidi.edge.api.TagDTO;

/**
 * This represents a read cycle from a reader. It contains 0 to many
 * TagReadEvents. The ReadCycle is a collection of tags read during a period of
 * time.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class ReadCycle implements Serializable {

	/** The serial version ID for this class */
	private static final long serialVersionUID = 1L;
	/** The tags that were read during this ReadCycle */
	private final TagReadEvent[] tags;
	/** The time this event was generated */
	private long eventTimestamp;
	/** The ReaderID which this ReadCycle belongs to */
	private String readerID;

	/**
	 * Constructor.
	 * 
	 * @param tags
	 *            The tags that were read during this ReadCycle
	 * @param eventTimestamp
	 *            The timestamp of when this ReadCycle was generated
	 */
	public ReadCycle(Set<TagReadEvent> tags, String readerID,
			long eventTimestamp) {
		this.tags = tags.toArray(new TagReadEvent[0]);
		this.eventTimestamp = eventTimestamp;
		this.readerID = readerID;
	}

	/**
	 * 
	 * @return An array of TagReadEvent objects that represent the data read
	 *         during this cycle.
	 */
	public TagReadEvent[] getTags() {
		return tags;
	}

	/**
	 * 
	 * @return the eventTimestamp
	 */
	public long getEventTimestamp() {
		return eventTimestamp;
	}

	/**
	 * 
	 * @return The ID of the reader that produced this ReadCycle
	 */
	public String getReaderID() {
		return this.readerID;
	}
	
	public TagBatch getBatch(){
		Set<TagDTO> dtos = new HashSet<TagDTO>();
		for(TagReadEvent event : this.tags){
			dtos.add(event.getTagDTO());
		}
		return new TagBatch(readerID, eventTimestamp, dtos);
	}
}
