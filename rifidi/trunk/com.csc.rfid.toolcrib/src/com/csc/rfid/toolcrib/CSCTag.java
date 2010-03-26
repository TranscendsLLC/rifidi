/*
 *  CSCTag.java
 *
 *  Created:	Mar 8, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package com.csc.rfid.toolcrib;

/**
 * This class is a bean that represents a single tag read event for the toolcrib
 * application
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class CSCTag {
	/** The tag ID */
	private String epc;
	/** The ID of the reader */
	private String readerID;
	/** The speed the tag was moving */
	private Float speed;
	/** The Return signal strength indicator of the tag observation */
	private String rssi;
	/** The antenna the tag was seen at */
	private int antenna;

	/**
	 * Returns antenna the tag was last seen by.  
	 * 
	 * @return the antenna
	 */
	public int getAntenna() {
		return antenna;
	}

	/**
	 * Sets the antenna that the tag was last seen by.  
	 * 
	 * @param antenna
	 *            The antenna the tag was seen at
	 */
	public void setAntenna(int antenna) {
		this.antenna = antenna;
	}

	/**
	 * Returns the ID of the tag as a String.   
	 * 
	 * @return the epc
	 */
	public String getEpc() {
		return epc;
	}

	/**
	 * Sets the ID of the tag as a String.  
	 * 
	 * @param epc
	 *            The tag ID
	 */
	public void setEpc(String epc) {
		this.epc = epc;
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
	 * Sets the ID of the reader.  
	 * 
	 * @param readerID
	 *            the ID of the reader
	 */
	public void setReaderID(String readerID) {
		this.readerID = readerID;
	}

	/**
	 * The speed the tag was moving at.  
	 * 
	 * @return the speed
	 */
	public Float getSpeed() {
		return speed;
	}

	/**
	 * Sets the speed that the tag was moving at.  
	 * 
	 * @param speed
	 *            The speed the tag was moving
	 */
	public void setSpeed(Float speed) {
		this.speed = speed;
	}

	/**
	 * The Return signal strength indicator of the tag observation.
	 * 
	 * @return the rssi
	 */
	public String getRssi() {
		return rssi;
	}

	/**
	 * Sets the RSSI value for the tag.  
	 * 
	 * @param rssi
	 *            The Return signal strength indicator of the tag observation
	 */
	public void setRssi(String rssi) {
		this.rssi = rssi;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TAG: " + epc + " Reader: " + readerID + " Antenna: " + antenna
				+ " RSSI: " + rssi + " Spped: " + speed;
	}
}
