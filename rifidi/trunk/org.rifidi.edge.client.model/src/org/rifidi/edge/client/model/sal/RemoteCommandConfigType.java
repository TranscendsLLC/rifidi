/**
 * 
 */
package org.rifidi.edge.client.model.sal;

import javax.management.MBeanInfo;

/**
 * @author kyle
 * 
 */
public class RemoteCommandConfigType {

	private String commandConfigType;

	private MBeanInfo mbean;

	public RemoteCommandConfigType(String typeID) {
		this.commandConfigType = typeID;
	}

	/**
	 * @return the type
	 */
	public String getCommandConfigType() {
		return commandConfigType;
	}

}
