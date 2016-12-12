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
 * @author matt
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "appstopped")
public class AppStoppedDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4378884169150770428L;
	@XmlElement(name = "group")
	private String group;
	@XmlElement(name = "name")
	private String name;
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
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
