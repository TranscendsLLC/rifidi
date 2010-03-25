package com.csc.rfid.toolcrib;

/**
 * This class is a bean that represents a single tag read event for the toolcrib
 * application
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
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
	 * The antenna the tag was seen at
	 * 
	 * @return the antenna
	 */
	public int getAntenna() {
		return antenna;
	}

	/**
	 * @param antenna
	 *            The antenna the tag was seen at
	 */
	public void setAntenna(int antenna) {
		this.antenna = antenna;
	}

	/**
	 * The tag ID
	 * 
	 * @return the epc
	 */
	public String getEpc() {
		return epc;
	}

	/**
	 * @param epc
	 *            The tag ID
	 */
	public void setEpc(String epc) {
		this.epc = epc;
	}

	/**
	 * the ID of the reader
	 * 
	 * @return the readerID
	 */
	public String getReaderID() {
		return readerID;
	}

	/**
	 * @param readerID
	 *            the ID of the reader
	 */
	public void setReaderID(String readerID) {
		this.readerID = readerID;
	}

	/**
	 * The speed the tag was moving
	 * 
	 * @return the speed
	 */
	public Float getSpeed() {
		return speed;
	}

	/**
	 * @param speed
	 *            The speed the tag was moving
	 */
	public void setSpeed(Float speed) {
		this.speed = speed;
	}

	/**
	 * The Return signal strength indicator of the tag observation
	 * 
	 * @return the rssi
	 */
	public String getRssi() {
		return rssi;
	}

	/**
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
