/*
 * 
 * CommandManagerServiceImpl.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
package org.rifidi.edge.rmi;

import java.util.HashSet;
import java.util.Set;

import javax.management.AttributeList;
import javax.management.MBeanInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.CommandConfigFactoryDTO;
import org.rifidi.edge.api.CommandConfigurationDTO;
import org.rifidi.edge.api.CommandManagerService;
import org.rifidi.edge.configuration.AbstractCommandConfigurationFactory;
import org.rifidi.edge.configuration.Configuration;
import org.rifidi.edge.configuration.ConfigurationService;
import org.rifidi.edge.daos.CommandDAO;
import org.rifidi.edge.exceptions.CannotCreateServiceException;
import org.rifidi.edge.sensors.AbstractCommandConfiguration;

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
	 * org.rifidi.edge.api.rmi.services.CommandManagerService#createCommand(
	 * java.lang.String, javax.management.AttributeList)
	 */
	@Override
	public void createCommand(String commandConfigurationType,
			AttributeList properties) {
		logger.info("RMI: Create Command called");
		try {
			configurationService.createService(commandConfigurationType,
					properties);
		} catch (CannotCreateServiceException e) {
			logger.warn("Command Configuraiton not created");
		}
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
		logger.debug("RMI: Delete Command Called");
		configurationService.destroyService(commandConfigurationID);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.CommandConfigurationStub#
	 * getCommandConfigurationDescription(java.lang.String)
	 */
	@Override
	public MBeanInfo getCommandDescription(String commandConfigurationType) {
		logger.debug("RMI: Get Command Description called");
		AbstractCommandConfigurationFactory<?> factory = this.commandDAO
				.getCommandFactory(commandConfigurationType);
		return factory.getServiceDescription(commandConfigurationType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.CommandConfigurationStub#
	 * getCommandConfigurationTypes()
	 */
	@Override
	public Set<CommandConfigFactoryDTO> getCommandConfigFactories() {
		logger.debug("RMI: Get Command Config Factories Called");
		Set<CommandConfigFactoryDTO> retVal = new HashSet<CommandConfigFactoryDTO>();
		for (AbstractCommandConfigurationFactory<?> factory : commandDAO
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
			String commandfactoryID) {
		logger.debug("RMI: Get Command Config Factory called");
		AbstractCommandConfigurationFactory<?> factory = commandDAO
				.getCommandFactory(commandfactoryID);
		if (factory != null) {
			return factory.getDTO();
		}
		return null;
	}

	@Override
	public Set<CommandConfigFactoryDTO> getCommandConfigFactoriesByReaderID(
			String readerFactoryID) {
		logger.debug("RMI: Get Command Config Factories By Reader ID called");
		Set<AbstractCommandConfigurationFactory<?>> factories = commandDAO
				.getCommandFactoryByReaderID(readerFactoryID);
		Set<CommandConfigFactoryDTO> dtos = new HashSet<CommandConfigFactoryDTO>();
		if (factories != null) {
			for (AbstractCommandConfigurationFactory<?> factory : factories) {
				dtos.add(factory.getDTO());
			}
		}
		return dtos;
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
		logger.debug("RMI: Get Commands Called");
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
		logger.debug("RMI: Get Command Configuration Called");
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
	public void setCommandProperties(String commandConfigurationID,
			AttributeList properties) {
		Configuration config = configurationService
				.getConfiguration(commandConfigurationID);
		config.setAttributes(properties);
	}

}
