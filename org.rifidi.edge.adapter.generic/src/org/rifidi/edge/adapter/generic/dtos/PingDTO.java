/**
 * 
 */
package org.rifidi.edge.adapter.generic.dtos;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Matthew Dean - matt@transcends.co
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "response")
public class PingDTO implements Serializable {
	
	private static final long serialVersionUID = 80368963942839368L;
	
	
	@XmlElement(name = "timestamp")
	private String timestamp;

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
}
