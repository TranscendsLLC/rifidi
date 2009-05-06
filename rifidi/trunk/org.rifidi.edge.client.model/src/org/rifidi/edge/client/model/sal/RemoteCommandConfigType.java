/**
 * 
 */
package org.rifidi.edge.client.model.sal;
//TODO: Comments
import javax.management.MBeanInfo;

/**
 * @author kyle
 * 
 */
public class RemoteCommandConfigType {

	private String commandConfigType;

	private MBeanInfo mbeanInfo;

	private String readerFactoryID;

	public RemoteCommandConfigType(String typeID, String readerFactoryID,
			MBeanInfo info) {
		this.commandConfigType = typeID;
		this.readerFactoryID = readerFactoryID;
		this.mbeanInfo = info;
	}

	/**
	 * @return the mbeanInfo
	 */
	public MBeanInfo getMbeanInfo() {
		return mbeanInfo;
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
