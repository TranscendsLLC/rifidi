/**
 * 
 */
package org.rifidi.edge.tools.notification;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author matt
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "sensorclosed")
public class SensorClosedDTO implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8622690951227519938L;
	
	@XmlElement(name = "sensor")
	private String sensor;
	@XmlElement(name = "timestamp")
	private Long timestamp;
	@XmlElement(name = "ip")
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
