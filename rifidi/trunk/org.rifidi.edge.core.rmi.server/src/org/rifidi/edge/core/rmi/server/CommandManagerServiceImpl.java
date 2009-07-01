package org.rifidi.edge.core.rmi.server;

import java.util.HashSet;
import java.util.Set;

import javax.management.AttributeList;
import javax.management.MBeanInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.configuration.Configuration;
import org.rifidi.configuration.services.ConfigurationService;
import org.rifidi.edge.api.rmi.services.CommandManagerService;
import org.rifidi.edge.api.rmi.dto.CommandConfigFactoryDTO;
import org.rifidi.edge.api.rmi.dto.CommandConfigurationDTO;
import org.rifidi.edge.core.daos.CommandDAO;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfigurationFactory;

/**
 * The Implementation of the CommandStub -- an RMI stub for managing
 * CommandConfigurations (creating, deleting, changing properties, etc)
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class CommandManagerServiceImpl implements CommandManagerService {

	/** Data Access object for accessing command configurations and factories */
	private CommandDAO commandDAO;
	/** Data access object for accessing all Configuration objects in OSGi */
	private ConfigurationService configurationService;
	/** The logger for this class */
	private static final Log logger = LogFactory
			.getLog(CommandManagerServiceImpl.class);

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
	 * @param configurationService
	 *            the configurationService to set
	 */
	public void setConfigurationService(
			ConfigurationService configurationService) {
		this.configurationService = configurationService;
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
			AttributeList properties) {
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
	public void deleteCommand(String commandConfigurationID) {
		Configuration config = configurationService
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
	public MBeanInfo getCommandDescription(String commandConfigurationType) {
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
	public Set<CommandConfigFactoryDTO> getCommandConfigFactories() {
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
	 * @see org.rifidi.edge.api.rmi.CommandStub#getCommandConfigFactory(java
	 * .lang.String)
	 */
	@Override
	public CommandConfigFactoryDTO getCommandConfigFactory(
			String readerFactoryID) {
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
	public Set<CommandConfigurationDTO> getCommands() {
		Set<CommandConfigurationDTO> retVal = new HashSet<CommandConfigurationDTO>();
		for (AbstractCommandConfiguration<?> commandconfig : commandDAO
				.getCommands()) {
			Configuration configObj = configurationService
					.getConfiguration(commandconfig.getID());
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
	 * @see org.rifidi.edge.api.rmi.CommandStub#getCommandConfiguration(java
	 * .lang.String)
	 */
	@Override
	public CommandConfigurationDTO getCommandConfiguration(
			String commandConfigurationID) {
		AbstractCommandConfiguration<?> commandConfig = commandDAO
				.getCommandByID(commandConfigurationID);
		Configuration configObj = configurationService
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
			AttributeList properties) {
		Configuration config = configurationService
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
