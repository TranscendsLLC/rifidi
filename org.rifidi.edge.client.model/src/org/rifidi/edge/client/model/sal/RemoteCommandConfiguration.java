/**
 * 
 */
package org.rifidi.edge.client.model.sal;
//TODO: Comments
import javax.management.AttributeList;

import org.rifidi.edge.client.model.sal.commands.RequestExecuterSingleton;
import org.rifidi.edge.core.api.rmi.dto.CommandConfigurationDTO;

/**
 * @author kyle
 * 
 */
public class RemoteCommandConfiguration extends
		AbstractAttributeContributorModelObject {

	/** The ID of the command */
	private String id;
	/** The Configuraiton Factory which created this configuration */
	private RemoteCommandConfigFactory factory;
	/** The type of the command */
	private RemoteCommandConfigType remoteCommandType;

	/**
	 * Constructor
	 * 
	 * @param dto
	 *            The DTO for the RemoteCommandConfiguration
	 * @param factory
	 *            The factory that created this commandconfig
	 */
	public RemoteCommandConfiguration(CommandConfigurationDTO dto,
			RemoteCommandConfigFactory factory) {
		super(dto.getCommandConfigID(), dto.getAttributes());
		this.id = dto.getCommandConfigID();
		this.factory = factory;
		this.remoteCommandType = factory.getCommandType(dto
				.getCommandConfigType());
	}

	/**
	 * 
	 * @return The ID of the type
	 */
	public String getCommandType() {
		return remoteCommandType.getCommandConfigType();
	}

	/**
	 * 
	 * @return The ID of the command configuration
	 */
	public String getID() {
		return id;
	}

	/**
	 * @return the readerFactoryID
	 */
	public String getReaderFactoryID() {
		return factory.getReaderFactoryID();
	}

	/**
	 * @return the factory
	 */
	public RemoteCommandConfigFactory getFactory() {
		return factory;
	}

	/**
	 * @return the Remote Command Type object
	 */
	public RemoteCommandConfigType getRemoteType() {
		return remoteCommandType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.model.sal.AbstractAttributeContributorModelObject
	 * #doSynchAttributeChange
	 * (org.rifidi.edge.client.model.sal.RemoteEdgeServer, java.lang.String,
	 * javax.management.AttributeList)
	 */
	@Override
	protected void doSynchAttributeChange(RemoteEdgeServer server,
			String modelID, AttributeList list) {

		Command_SynchCommandConfigPropertyChanges command = new Command_SynchCommandConfigPropertyChanges(
				server, modelID, list);
		RequestExecuterSingleton.getInstance().scheduleRequest(command);

	}

}
