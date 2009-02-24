/**
 * 
 */
package org.rifidi.edge.core.rmi.internal;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.AttributeList;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.configuration.Configuration;
import org.rifidi.edge.core.commands.AbstractCommandConfigurationFactory;
import org.rifidi.edge.core.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.internal.CommandConfigurationDAO;
import org.rifidi.edge.core.internal.ConfigurationDAO;
import org.rifidi.edge.core.internal.ReaderConfigurationDAO;
import org.rifidi.edge.core.readers.AbstractReaderConfigurationFactory;
import org.rifidi.edge.core.rmi.CommandConfigurationStub;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class CommandConfigurationStubImpl implements CommandConfigurationStub {

	/** Data Access object for accessing command configurations and factories */
	private CommandConfigurationDAO commandConfigDAO;
	/** Data Access object for accessing reader configurations and factories */
	private ReaderConfigurationDAO readerConfigDAO;
	/** Data access object for accessing all Configuration objects in OSGi */
	private ConfigurationDAO configDAO;
	/** The logger for this class */
	private static final Log logger = LogFactory
			.getLog(CommandConfigurationStubImpl.class);

	/**
	 * Setter method used by spring
	 * 
	 * @param configConfigDAO
	 *            the configConfigDAO to set
	 */
	public void setCommandConfigDAO(CommandConfigurationDAO commandConfigDAO) {
		this.commandConfigDAO = commandConfigDAO;
	}

	/**
	 * Setter method used by spring
	 * 
	 * @param readerConfigDAO
	 *            the readerConfigDAO to set
	 */
	public void setReaderConfigDAO(ReaderConfigurationDAO readerConfigDAO) {
		this.readerConfigDAO = readerConfigDAO;
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
	public String createCommandConfiguration(String commandConfigurationType,
			AttributeList properties) throws RemoteException {
		AbstractCommandConfigurationFactory factory = this.commandConfigDAO
				.getCommandConfigurationFactoryFromType(commandConfigurationType);
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
	public void deleteCommandConfiguration(String commandConfigurationID)
			throws RemoteException {
		Configuration config = configDAO
				.getConfiguration(commandConfigurationID);
		if (config != null) {
			config.destroy();
		} else {
			logger.warn("No Configuration with ID " + commandConfigurationID
					+ " found");
		}

		AbstractCommandConfiguration<?> commandConfig = commandConfigDAO
				.getCommandConfiguration(commandConfigurationID);
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
	public MBeanInfo getCommandConfigurationDescription(
			String commandConfigurationType) throws RemoteException {
		AbstractCommandConfigurationFactory factory = this.commandConfigDAO
				.getCommandConfigurationFactoryFromType(commandConfigurationType);
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
	public AttributeList getCommandConfigurationProperties(
			String commandConfigurationID) throws RemoteException {
		Configuration config = configDAO
				.getConfiguration(commandConfigurationID);
		if (config != null) {
			// find out the names of all the attributes
			List<String> attributeNames = new ArrayList<String>();
			for (MBeanAttributeInfo attrInfo : config.getMBeanInfo()
					.getAttributes()) {
				attributeNames.add(attrInfo.getName());
			}

			// convert name arraylist to string array
			String[] names = new String[attributeNames.size()];
			attributeNames.toArray(names);

			// get the attributes
			return config.getAttributes(names);
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
		Set<AbstractReaderConfigurationFactory<?>> factories = readerConfigDAO
				.getCurrentReaderConfigurationFactories();
		Iterator<AbstractReaderConfigurationFactory<?>> factoryIter = factories
				.iterator();
		while (factoryIter.hasNext()) {
			AbstractReaderConfigurationFactory<?> factory = factoryIter.next();
			String factoryID = factory.getCommandConfigFactoryID();
			Set<String> types = this.commandConfigDAO
					.getCommandConfigurationTypes(factoryID);
			Iterator<String> typeIter = types.iterator();
			while (typeIter.hasNext()) {
				retVal.put(typeIter.next(), factoryID);
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
	public Map<String, String> getCommandConfigurations()
			throws RemoteException {
		Map<String, String> retVal = new HashMap<String, String>();
		Set<AbstractCommandConfiguration<?>> configuraitons = this.commandConfigDAO
				.getCommandConfigurations();
		Iterator<AbstractCommandConfiguration<?>> iter = configuraitons
				.iterator();
		while (iter.hasNext()) {
			AbstractCommandConfiguration<?> commandconfig = iter.next();
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
	public AttributeList setCommandConfigurationProperties(
			String commandConfigurationID, AttributeList properties)
			throws RemoteException {
		Configuration config = configDAO.getConfiguration(commandConfigurationID);
		if (config != null) {
			return config.setAttributes(properties);
		} else {
			logger.warn("No Configuration object with ID "
					+ commandConfigurationID + " is available");
		}
		return null;
	}

}
