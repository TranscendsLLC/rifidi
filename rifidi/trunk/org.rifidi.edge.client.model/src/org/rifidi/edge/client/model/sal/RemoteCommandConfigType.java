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
	
	private String readerFactoryID;

	public RemoteCommandConfigType(String typeID, String readerFactoryID) {
		this.commandConfigType = typeID;
		this.readerFactoryID = readerFactoryID;
	}

	/**
	 * @return the type
	 */
	public String getCommandConfigType() {
		return commandConfigType;
	}

	/**
	 * @return the readerFactoryID
	 */
	public String getReaderFactoryID() {
		return readerFactoryID;
	}
}
