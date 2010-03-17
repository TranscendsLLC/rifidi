/*
 *  RFIDEvent.java
 *
 *  Created:	Mar 17, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.core.app.api.events;

/**
 * Represents a read of a specific RFID tag on a specific antenna.
 * 
 * @author Matthew Dean
 */
public abstract class RFIDEvent {

	private Integer antenna = -1;

	private String epc = "";

	private String readerID = "";

	/**
	 * @return the epc
	 */
	public String getEpc() {
		return epc;
	}

	/**
	 * @param epc
	 *            the epc to set
	 */
	public void setEpc(String epc) {
		this.epc = epc;
	}

	/**
	 * @return the antenna
	 */
	public Integer getAntenna() {
		return antenna;
	}

	/**
	 * @param antenna
	 *            the antenna to set
	 */
	public void setAntenna(Integer antenna) {
		this.antenna = antenna;
	}

	/**
	 * @return the readerID
	 */
	public String getReaderID() {
		return readerID;
	}

	/**
	 * @param readerID
	 *            the readerID to set
	 */
	public void setReaderID(String readerID) {
		this.readerID = readerID;
	}
}
