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

import java.math.BigInteger;

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
	 * 
	 */
	public RSSITagReadEvent(String tagID, String readerID, Integer antenna, Double avgRSSI,
			Double tagCount) {
		this.tagID = tagID;
		this.readerID = readerID;
		this.avgRSSI = avgRSSI;
		this.tagCount = tagCount;
		this.antenna=antenna;
	}

	public String getTagID() {
		return tagID;
	}

	public void setTagID(String tagID) {
		this.tagID = tagID;
	}

	public String getReaderID() {
		return readerID;
	}

	public void setReaderID(String readerID) {
		this.readerID = readerID;
	}

	public Double getAvgRSSI() {
		return avgRSSI;
	}

	public void setAvgRSSI(Double avgRSSI) {
		this.avgRSSI = avgRSSI;
	}

	public Integer getAntenna() {
		return antenna;
	}

	public void setAntenna(Integer antenna) {
		this.antenna = antenna;
	}

	public void setTagCount(Double tagCount) {
		this.tagCount = tagCount;
	}

	public Double getTagCount() {
		return this.tagCount;
	}
	
	public String getReadZone() {
		return readZone;
	}

	public void setReadZone(String readzone) {
		this.readZone = readzone;
	}

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

	public String getCombinedReaderTagID() {
		return tagID + "" + readerID;
	}
}
