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
@XmlRootElement(name = "keepalive")
public class KeepAliveDTO implements Serializable {
	
	private static final long serialVersionUID = 6327870357250109302L;
	
	@XmlElement(name = "ip")
	private String ip;
	@XmlElement(name = "name")
	private String name;
	@XmlElement(name = "timestamp")
	private Long timestamp;
	@XmlElement(name = "uptime")
	private Long uptime;
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	public Long getUptime() {
		return uptime;
	}
	public void setUptime(Long uptime) {
		this.uptime = uptime;
	}	
	
}
