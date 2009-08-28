package org.rifidi.edge.client.model.sal;

import javax.management.MBeanInfo;

import org.rifidi.edge.api.rmi.dto.CommandConfigFactoryDTO;

/**
 * The Model object for A CommandConfigurationFactory
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class RemoteCommandConfigFactory {

	/** The ID of the reader this factory is associated with */
	private String readerFactoryID;
	/** The ID of the type */
	private String commandConfigFactoryID;
	/** The MBeanInfo that describes the properties of this type */
	private MBeanInfo mbeanInfo;

	/**
	 * Constructor.
	 * 
	 * @param dto
	 *            the Data Transfer Object for the CommandConfigurationFactory
	 * @param typeToMbeanInfo
	 *            A map of types to their related MBeanInfos
	 */
	public RemoteCommandConfigFactory(CommandConfigFactoryDTO dto,
			MBeanInfo mbeanInfo) {
		this.readerFactoryID = dto.getReaderFactoryID();
		this.commandConfigFactoryID = dto.getCommandFactoryID();
		this.mbeanInfo = mbeanInfo;
	}

	/**
	 * @return the readerFactoryID
	 */
	public String getReaderFactoryID() {
		return readerFactoryID;
	}

	/**
	 * @return the commandConfigID
	 */
	public String getCommandConfigFactoryID() {
		return commandConfigFactoryID;
	}

	/**
	 * @return the mbeanInfo
	 */
	public MBeanInfo getMbeanInfo() {
		return mbeanInfo;
	}
	
	

}
