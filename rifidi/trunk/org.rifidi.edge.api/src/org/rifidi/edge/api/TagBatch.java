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
package org.rifidi.edge.api;

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
