/**
 * 
 */
package org.rifidi.edge.client.model.sal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanInfo;

import org.rifidi.edge.core.api.rmi.dto.CommandConfigFactoryDTO;

/**
 * @author kyle
 * 
 */
public class RemoteCommandConfigFactory {

	private String readerFactoryID;
	private HashMap<String, RemoteCommandConfigType> commandTypes;

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
	 * 
	 * @return the commandtypes associated with this CommandConfigFactory
	 */
	public Set<RemoteCommandConfigType> getCommandTypes() {
		return new HashSet<RemoteCommandConfigType>(commandTypes.values());
	}

	public RemoteCommandConfigType getCommandType(String commandType) {
		return commandTypes.get(commandType);
	}

	public boolean containsType(String commandType) {
		return this.commandTypes.keySet().contains(commandType);
	}

}
