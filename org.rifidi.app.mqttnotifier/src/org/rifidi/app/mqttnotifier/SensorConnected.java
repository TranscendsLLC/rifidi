package org.rifidi.app.mqttnotifier;

import java.io.Serializable;

public class SensorConnected implements Serializable {
	
		
	/**
	 * 
	 */
	private static final long serialVersionUID = -6217918715374788022L;
	private String sensor;
	private Long timestamp;
	private String ip;
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}	
	public String getSensor() {
		return sensor;
	}
	public void setSensor(String sensor) {
		this.sensor = sensor;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}	
}
