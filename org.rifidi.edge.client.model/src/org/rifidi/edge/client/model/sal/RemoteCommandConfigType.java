
package org.rifidi.edge.client.model.sal;

import javax.management.MBeanInfo;

/**
 * TODO: Class level comment.  
 * 
 * @author kyle
 */
public class RemoteCommandConfigType {

	private String commandConfigType;

	private MBeanInfo mbeanInfo;

	private String readerFactoryID;

	/**
	 * Constructor.  
	 * 
	 * @param typeID
	 * @param readerFactoryID
	 * @param info
	 */
	public RemoteCommandConfigType(String typeID, String readerFactoryID,
			MBeanInfo info) {
		this.commandConfigType = typeID;
		this.readerFactoryID = readerFactoryID;
		this.mbeanInfo = info;
	}

	/**
	 * TODO: Method level comment.  
	 * 
	 * @return the mbeanInfo
	 */
	public MBeanInfo getMbeanInfo() {
		return mbeanInfo;
	}

	/**
	 * TODO: Method level comment.  
	 * 
	 * @return the type
	 */
	public String getCommandConfigType() {
		return commandConfigType;
	}

	/**
	 * TODO: Method level comment.  
	 * 
	 * @return the readerFactoryID
	 */
	public String getReaderFactoryID() {
		return readerFactoryID;
	}
}
