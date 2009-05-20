package org.rifidi.edge.client.model.sal;

import javax.management.MBeanInfo;

/**
 * Model Object for a CommandConfigurationType
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class RemoteCommandConfigType {

	/** The ID of the type */
	private String commandConfigType;
	/** The MBeanInfo that describes the properties of this type */
	private MBeanInfo mbeanInfo;
	/**
	 * The ID of the ReaderFactory this type produces command configurations for
	 */
	private String readerFactoryID;

	/**
	 * Constructor.
	 * 
	 * @param typeID
	 *            THe ID of the type
	 * @param readerFactoryID
	 *            The ID of the readerFactory this commandtype works with
	 * @param info
	 *            the MBeanInfo that describes the attributes associated with
	 *            this type
	 */
	public RemoteCommandConfigType(String typeID, String readerFactoryID,
			MBeanInfo info) {
		this.commandConfigType = typeID;
		this.readerFactoryID = readerFactoryID;
		this.mbeanInfo = info;
	}

	/**
	 * The MBeanInfo describes the attributes (types, min/mix, categories,
	 * description, etc)
	 * 
	 * @return the mbeanInfo
	 */
	public MBeanInfo getMbeanInfo() {
		return mbeanInfo;
	}

	/**
	 * 
	 * @return The ID of the type
	 */
	public String getCommandConfigType() {
		return commandConfigType;
	}

	/**
	 * 
	 * @return The ID of the reader factory this type produces command
	 *         configuraitons for
	 */
	public String getReaderFactoryID() {
		return readerFactoryID;
	}
}
