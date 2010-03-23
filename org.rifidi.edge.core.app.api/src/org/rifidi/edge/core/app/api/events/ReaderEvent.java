/*
 *  ReaderEvent.java
 *
 *  Created:	Mar 23, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.core.app.api.events;

/**
 * Represents a reader going up or down.  
 * 
 * @author Matthew Dean
 */
public abstract class ReaderEvent {
	
	private String readerID;
	
	
	private long time;

	/**
	 * The time in milliseconds that the reader went down.  
	 * 
	 * @return the time
	 */
	public long getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(long time) {
		this.time = time;
	}

	/**
	 * @return the readerID
	 */
	public String getReaderID() {
		return readerID;
	}

	/**
	 * @param readerID the readerID to set
	 */
	public void setReaderID(String readerID) {
		this.readerID = readerID;
	}
}
