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
 * Represents and antenna coming online
 * 
 * @author Matthew Dean - matt@transcends.co
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "antennaup")
public class AntennaDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5324534795492066991L;
	
	@XmlElement(name = "sensor")
	private String sensor;
	@XmlElement(name = "antenna")
	private Integer antenna;
	@XmlElement(name = "timestamp")
	private Long timestamp;
	@XmlElement(name = "up")
	private Boolean up;
	
	public String getSensor() {
		return sensor;
	}
	public void setSensor(String sensor) {
		this.sensor = sensor;
	}
	public Integer getAntenna() {
		return antenna;
	}
	public void setAntenna(Integer antenna) {
		this.antenna = antenna;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	public Boolean isUp() {
		return up;
	}
	public void setUp(Boolean up) {
		this.up = up;
	}
	
}
