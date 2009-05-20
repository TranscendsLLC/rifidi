package org.rifidi.edge.client.model.sal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanInfo;

import org.rifidi.edge.core.api.rmi.dto.CommandConfigFactoryDTO;

/**
 * The Model object for A CommandConfigurationFactory
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class RemoteCommandConfigFactory {

	/** The ID of the reader this factory is associated with */
	private String readerFactoryID;
	/** The types of commands this factory can produce */
	private HashMap<String, RemoteCommandConfigType> commandTypes;

	/**
	 * Constructor.
	 * 
	 * @param dto
	 *            the Data Transfer Object for the CommandConfigurationFactory
	 * @param typeToMbeanInfo
	 *            A map of types to their related MBeanInfos
	 */
	public RemoteCommandConfigFactory(CommandConfigFactoryDTO dto,
			Map<String, MBeanInfo> typeToMbeanInfo) {
		this.readerFactoryID = dto.getReaderFactoryID();
		commandTypes = new HashMap<String, RemoteCommandConfigType>();
		for (String typeID : dto.getCommandConfigTypeIDs()) {
			commandTypes.put(typeID, new RemoteCommandConfigType(typeID,
					readerFactoryID, typeToMbeanInfo.get(typeID)));
		}
	}

	/**
	 * @return the readerFactoryID
	 */
	public String getReaderFactoryID() {
		return readerFactoryID;
	}

	/**
	 * @return the commandtypes associated with this CommandConfigFactory
	 */
	public Set<RemoteCommandConfigType> getCommandTypes() {
		return new HashSet<RemoteCommandConfigType>(commandTypes.values());
	}

	/**
	 * Return a CommandConfigType model object for the ID of the type
	 * 
	 * @param commandType
	 *            The ID of the type
	 * @return The Type model object
	 */
	public RemoteCommandConfigType getCommandType(String commandType) {
		return commandTypes.get(commandType);
	}

	/**
	 * 
	 * @param commandType
	 *            the ID of the ComamndType
	 * @return true if this factory can produces command configurations of the
	 *         given type
	 */
	public boolean containsType(String commandType) {
		return this.commandTypes.keySet().contains(commandType);
	}

}
