/**
 * 
 */
package org.rifidi.edge.notification;

import java.io.Serializable;

/**
 * @author matt
 *
 */
public class AppStartedEvent implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 97543249326137511L;
	
	private String group;
	private String name;
	
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
