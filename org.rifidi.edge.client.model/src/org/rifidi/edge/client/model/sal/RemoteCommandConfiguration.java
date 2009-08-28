package org.rifidi.edge.client.model.sal;

import javax.management.AttributeList;

import org.rifidi.edge.client.model.sal.commands.RequestExecuterSingleton;
import org.rifidi.edge.api.rmi.dto.CommandConfigurationDTO;

/**
 * A model object for RemoteCommandConfigurations
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class RemoteCommandConfiguration extends
		AbstractAttributeContributorModelObject {

	/** The ID of the command */
	private String id;
	/** The Configuraiton Factory which created this configuration */
	private RemoteCommandConfigFactory factory;

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
	}

	/**
	 * Returns the command type.
	 * 
	 * @return The ID of the type
	 */
	public String getCommandType() {
		return factory.getCommandConfigFactoryID();
	}

	/**
	 * Returns the ID.
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
	 * Returns the factory.
	 * 
	 * @return the factory
	 */
	public RemoteCommandConfigFactory getFactory() {
		return factory;
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
