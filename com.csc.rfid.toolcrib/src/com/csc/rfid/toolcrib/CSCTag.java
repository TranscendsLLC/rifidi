/**
 * 
 */
package com.csc.rfid.toolcrib;

/**
 * @author Owner
 *
 */
public class CSCTag {
	private String epc;
	private String readerID;
	private Float speed;
	private String rssi;
	/**
	 * @return the epc
	 */
	public String getEpc() {
		return epc;
	}
	/**
	 * @param epc the epc to set
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
	 * @param readerID the readerID to set
	 */
	public void setReaderID(String readerID) {
		this.readerID = readerID;
	}
	/**
	 * @return the speed
	 */
	public Float getSpeed() {
		return speed;
	}
	/**
	 * @param speed the speed to set
	 */
	public void setSpeed(Float speed) {
		this.speed = speed;
	}
	/**
	 * @return the rssi
	 */
	public String getRssi() {
		return rssi;
	}
	/**
	 * @param rssi the rssi to set
	 */
	public void setRssi(String rssi) {
		this.rssi = rssi;
	}
	
	
}
