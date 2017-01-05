/**
 * 
 */
package org.rifidi.app.mqttnotifier;

import java.io.Serializable;

/**
 * @author matt
 *
 */
public class AppStopped implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4378884169150770428L;
	private String group;
	private String name;
	private Long timestamp;
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
