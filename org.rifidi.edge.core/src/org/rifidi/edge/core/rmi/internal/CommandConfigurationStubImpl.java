/**
 * 
 */
package org.rifidi.edge.core.rmi.internal;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import javax.management.AttributeList;
import javax.management.MBeanInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.configuration.Configuration;
import org.rifidi.edge.core.api.rmi.CommandStub;
import org.rifidi.edge.core.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.commands.AbstractCommandConfigurationFactory;
import org.rifidi.edge.core.daos.CommandDAO;
import org.rifidi.edge.core.daos.ConfigurationDAO;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * 
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
		Configuration configuration = factory
				.getEmptyConfiguration(commandConfigurationType);
		configuration.setAttributes(properties);
		factory.createService(configuration);
		return configuration.getServiceID();
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
	 * getCommandConfigurationProperties(java.lang.String)
	 */
	@Override
	public AttributeList getCommandProperties(String commandConfigurationID)
			throws RemoteException {
		Configuration config = configDAO
				.getConfiguration(commandConfigurationID);
		if (config != null) {
			// get the attributes
			return config.getAttributes(config.getAttributeNames());
		} else {
			logger.warn("No Configuration object with ID "
					+ commandConfigurationID + " is available");
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.CommandConfigurationStub#
	 * getCommandConfigurationTypes()
	 */
	@Override
	public Map<String, String> getCommandConfigurationTypes()
			throws RemoteException {
		Map<String, String> retVal = new HashMap<String, String>();
		for (AbstractCommandConfigurationFactory factory : commandDAO
				.getCommandFactories()) {
			for (String facID : factory.getFactoryIDs()) {
				retVal.put(facID, factory.getReaderFactoryID());
			}
		}

		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.rmi.CommandConfigurationStub#getCommandConfigurations
	 * (java.lang.String)
	 */
	@Override
	public Map<String, String> getCommands() throws RemoteException {
		Map<String, String> retVal = new HashMap<String, String>();
		for(AbstractCommandConfiguration<?> commandconfig: commandDAO.getCommands()){
			Configuration configObj = configDAO.getConfiguration(commandconfig
					.getID());
			if (configObj != null) {
				retVal.put(commandconfig.getID(), configObj.getFactoryID());
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
