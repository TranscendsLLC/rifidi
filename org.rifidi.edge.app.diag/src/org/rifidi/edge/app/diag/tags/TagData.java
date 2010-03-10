package org.rifidi.edge.app.diag.tags;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TagData {

	private String epc;
	private String readerID;
	private int antenna;
	private long timestamp;
	private static SimpleDateFormat dateFormat;
	static {
		dateFormat = new SimpleDateFormat("yyyy.MMMMM.dd hh:mm:ss:SSS");
	}

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

	/**
	 * @return the antenna
	 */
	public int getAntenna() {
		return antenna;
	}

	/**
	 * @param antenna
	 *            the antenna to set
	 */
	public void setAntenna(int antenna) {
		this.antenna = antenna;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Tag:" + epc + " Reader: " + readerID + " Antenna: " + antenna +" "
				+ dateFormat.format(new Date(timestamp));
	}

}
