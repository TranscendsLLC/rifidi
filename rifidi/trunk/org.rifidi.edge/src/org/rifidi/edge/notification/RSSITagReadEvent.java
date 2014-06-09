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
/**
 * 
 */
package org.rifidi.edge.notification;

/**
 * 
 * 
 * @author rifidi
 */
public class RSSITagReadEvent {
	
	private String tagID;
	private String readerID;
	private Double avgRSSI;
	private Double tagCount;
	private Integer antenna;
	private String readZone;

	/**
	 * Constructor
	 * 
	 * @param tagID
	 * 			The ID of the tag
	 * @param readerID
	 * 			The ID of the reader
	 * @param antenna
	 * 			The antenna that the tag was seen on
	 * @param avgRSSI
	 * 			The average RSSI value that this tag was seen with
	 * @param tagCount
	 * 			The number of times the tag was seen
	 */
	public RSSITagReadEvent(String tagID, String readerID, Integer antenna, Double avgRSSI,
			Double tagCount) {
		this.tagID = tagID;
		this.readerID = readerID;
		this.avgRSSI = avgRSSI;
		this.tagCount = tagCount;
		this.antenna=antenna;
	}

	/**
	 * The ID of the tag
	 * 
	 * @return
	 */
	public String getTagID() {
		return tagID;
	}

	/**
	 * 
	 * @param tagID
	 */
	public void setTagID(String tagID) {
		this.tagID = tagID;
	}
	
	/**
	 * The ID of the reader that saw the tag
	 * 
	 * @return
	 */
	public String getReaderID() {
		return readerID;
	}
	
	/**
	 * 
	 * @param readerID
	 */
	public void setReaderID(String readerID) {
		this.readerID = readerID;
	}

	/**
	 * The average RSSI value that this tag was seen with
	 * 
	 * @return
	 */
	public Double getAvgRSSI() {
		return avgRSSI;
	}

	/**
	 * 
	 * @param avgRSSI
	 */
	public void setAvgRSSI(Double avgRSSI) {
		this.avgRSSI = avgRSSI;
	}

	/**
	 * The antenna the tag was seen on
	 * 
	 * @return
	 */
	public Integer getAntenna() {
		return antenna;
	}

	/**
	 * 
	 * @param antenna
	 */
	public void setAntenna(Integer antenna) {
		this.antenna = antenna;
	}

	/**
	 * 
	 * 
	 * @param tagCount
	 */
	public void setTagCount(Double tagCount) {
		this.tagCount = tagCount;
	}

	/**
	 * The number of times the tag was seen
	 * 
	 * @return
	 */
	public Double getTagCount() {
		return this.tagCount;
	}
	
	/**
	 * The readzone the reader was seen on
	 * 
	 * @return
	 */
	public String getReadZone() {
		return readZone;
	}

	/**
	 * 
	 * @param readzone
	 */
	public void setReadZone(String readzone) {
		this.readZone = readzone;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RSSITagReadEvent tag: " + this.tagID);
		builder.append(", reader: " + this.readerID);
		builder.append(", antenna: " + this.antenna);
		builder.append(", avg: " + this.avgRSSI);
		builder.append(", sum: " + this.tagCount);
		builder.append(", readzone: " + this.readZone);
		return builder.toString();
	}

	/**
	 * 
	 * @return
	 */
	public String getCombinedReaderTagID() {
		return tagID + "" + readerID;
	}
}
