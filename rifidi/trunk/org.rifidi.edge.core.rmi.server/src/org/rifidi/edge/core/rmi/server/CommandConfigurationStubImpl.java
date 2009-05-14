
package org.rifidi.edge.core.rmi.server;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;

import javax.management.AttributeList;
import javax.management.MBeanInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.configuration.Configuration;
import org.rifidi.edge.core.api.rmi.CommandStub;
import org.rifidi.edge.core.api.rmi.dto.CommandConfigFactoryDTO;
import org.rifidi.edge.core.api.rmi.dto.CommandConfigurationDTO;
import org.rifidi.edge.core.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.commands.AbstractCommandConfigurationFactory;
import org.rifidi.edge.core.daos.CommandDAO;
import org.rifidi.edge.core.daos.ConfigurationDAO;

/**
 * TODO: Class level comment.  
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class CommandConfigurationStubImpl implements CommandStub {

	/** Data Access object for accessing command configurations and factories */
	private CommandDAO commandDAO;
	/** Data access object for accessing all Configuration objects in OSGi */
	private ConfigurationDAO configDAO;
	/** The logger for this class */
	private static final Log logger = LogFactory
			.getLog(CommandConfigurationStubImpl.class);

	/**
	 * Setter method used by spring
	 * 
	 * @param commandDAO
	 *            the commandDAO to set
	 */
	public void setCommandDAO(CommandDAO commandDAO) {
		this.commandDAO = commandDAO;
	}

	/**
	 * Setter method used by spring
	 * 
	 * @param configDAO
	 *            the configDAO to set
	 */
	public void setConfigDAO(ConfigurationDAO configDAO) {
		this.configDAO = configDAO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.rmi.CommandConfigurationStub#createCommandConfiguration
	 * (java.lang.String, javax.management.AttributeList)
	 */
	@Override
	public String createCommand(String commandConfigurationType,
			AttributeList properties) throws RemoteException {
		AbstractCommandConfigurationFactory factory = this.commandDAO
				.getCommandFactoryByID(commandConfigurationType);
		if (factory != null) {
			Configuration configuration = factory
					.getEmptyConfiguration(commandConfigurationType);

			if (configuration != null) {
				configuration.setAttributes(properties);
				factory.createService(configuration);
				return configuration.getServiceID();
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.rmi.CommandConfigurationStub#deleteCommandConfiguration
	 * (java.lang.String)
	 */
	@Override
	public void deleteCommand(String commandConfigurationID)
			throws RemoteException {
		Configuration config = configDAO
				.getConfiguration(commandConfigurationID);
		if (config != null) {
			config.destroy();
		} else {
			logger.warn("No Configuration with ID " + commandConfigurationID
					+ " found");
		}

		AbstractCommandConfiguration<?> commandConfig = commandDAO
				.getCommandByID(commandConfigurationID);
		if (commandConfig != null) {
			commandConfig.destroy();
		} else {
			logger.warn("No Configuration with ID " + commandConfigurationID
					+ " found");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.CommandConfigurationStub#
	 * getCommandConfigurationDescription(java.lang.String)
	 */
	@Override
	public MBeanInfo getCommandDescription(String commandConfigurationType)
			throws RemoteException {
		AbstractCommandConfigurationFactory factory = this.commandDAO
				.getCommandFactoryByID(commandConfigurationType);
		if (factory == null) {
			logger.warn("No Command Configuration Factory"
					+ " is available for type : " + commandConfigurationType);
			return null;
		}

		Configuration config = factory
				.getEmptyConfiguration(commandConfigurationType);
		return config.getMBeanInfo();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.CommandConfigurationStub#
	 * getCommandConfigurationTypes()
	 */
	@Override
	public Set<CommandConfigFactoryDTO> getCommandConfigFactories()
			throws RemoteException {
		Set<CommandConfigFactoryDTO> retVal = new HashSet<CommandConfigFactoryDTO>();
		for (AbstractCommandConfigurationFactory factory : commandDAO
				.getCommandFactories()) {
			retVal.add(factory.getDTO());
		}

		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.api.rmi.CommandStub#getCommandConfigFactory(java
	 * .lang.String)
	 */
	@Override
	public CommandConfigFactoryDTO getCommandConfigFactory(
			String readerFactoryID) throws RemoteException {
		AbstractCommandConfigurationFactory factory = commandDAO
				.getCommandFactoryByReaderID(readerFactoryID);
		if (factory != null) {
			return factory.getDTO();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.rmi.CommandConfigurationStub#getCommandConfigurations
	 * (java.lang.String)
	 */
	@Override
	public Set<CommandConfigurationDTO> getCommands() throws RemoteException {
		Set<CommandConfigurationDTO> retVal = new HashSet<CommandConfigurationDTO>();
		for (AbstractCommandConfiguration<?> commandconfig : commandDAO
				.getCommands()) {
			Configuration configObj = configDAO.getConfiguration(commandconfig
					.getID());
			if (configObj != null) {
				retVal.add(commandconfig.getDTO(configObj));
			} else {
				logger.warn("Configuration Object with ID "
						+ commandconfig.getID() + " does not exist");
			}

		}
		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.api.rmi.CommandStub#getCommandConfiguration(java
	 * .lang.String)
	 */
	@Override
	public CommandConfigurationDTO getCommandConfiguration(
			String commandConfigurationID) throws RemoteException {
		AbstractCommandConfiguration<?> commandConfig = commandDAO
				.getCommandByID(commandConfigurationID);
		Configuration configObj = configDAO
				.getConfiguration(commandConfigurationID);
		if (commandConfig != null && configObj != null) {
			return commandConfig.getDTO(configObj);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.CommandConfigurationStub#
	 * setCommandConfigurationProperties(java.lang.String,
	 * javax.management.AttributeList)
	 */
	@Override
	public AttributeList setCommandProperties(String commandConfigurationID,
			AttributeList properties) throws RemoteException {
		Configuration config = configDAO
				.getConfiguration(commandConfigurationID);
		if (config != null) {
			return config.setAttributes(properties);
		} else {
			logger.warn("No Configuration object with ID "
					+ commandConfigurationID + " is available");
		}
		return null;
	}

}
