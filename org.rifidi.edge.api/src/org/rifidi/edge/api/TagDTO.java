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
import java.math.BigInteger;

/**
 * A data transfer object for a tag read. For serializing information about a
 * single tag event
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class TagDTO implements Serializable {

	/** SerialVersionID */
	private static final long serialVersionUID = 1L;

	private BigInteger tagID;

	private int antennaNumber;

	private long timestamp;

	/**
	 * Constructor.
	 * 
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
	 * Returns the ID for this tag.
	 * 
	 * @return the tagID
	 */
	public BigInteger getTagID() {
		return tagID;
	}

	/**
	 * Returns the antenna that this tag was seen on.
	 * 
	 * @return the antennaNumber
	 */
	public int getAntennaNumber() {
		return antennaNumber;
	}

	/**
	 * Returns the timestamp.
	 * 
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

}
