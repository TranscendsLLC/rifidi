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
 * 
 * 
 * @author Matthew Dean - matt@transcends.co
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "antennaup")
public class ReaderExceptionDTO implements Serializable {
	
	private static final long serialVersionUID = -2419766855643906060L;
	
	@XmlElement(name = "sensor")
	private String sensor;
	@XmlElement(name = "errordescription")
	private String errordescription;
	@XmlElement(name = "statuscode")
	private String statuscode;
	@XmlElement(name = "timestamp")
	private Long timestamp;
	
	
	public String getSensor() {
		return sensor;
	}
	public void setSensor(String sensor) {
		this.sensor = sensor;
	}
	public String getErrordescription() {
		return errordescription;
	}
	public void setErrordescription(String errordescription) {
		this.errordescription = errordescription;
	}
	public String getStatuscode() {
		return statuscode;
	}
	public void setStatuscode(String statuscode) {
		this.statuscode = statuscode;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	
}
