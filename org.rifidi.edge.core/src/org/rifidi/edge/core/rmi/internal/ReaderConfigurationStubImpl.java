/**
 * 
 */
package org.rifidi.edge.core.rmi.internal;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import org.rifidi.edge.core.internal.ConfigurationDAO;
import org.rifidi.edge.core.internal.ReaderConfigurationDAO;
import org.rifidi.edge.core.readers.AbstractReaderConfigurationFactory;
import org.rifidi.edge.core.readers.ReaderConfiguration;
import org.rifidi.edge.core.rmi.ReaderConfigurationStub;

/**
 * @author kyle
 * 
 */
public class ReaderConfigurationStubImpl implements ReaderConfigurationStub {

	/** A data access object for the reader configuration services */
	private ReaderConfigurationDAO readerConfigDAO;
	/** A data access object for configurations in OSGi */
	private ConfigurationDAO configurationDAO;
	/** A logger for this class */
	private static final Log logger = LogFactory
			.getLog(ReaderConfigurationStubImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.rmi.ReaderConfigurationStub#createReaderConfiguration
	 * (java.lang.String, javax.management.AttributeList)
	 */
	@Override
	public String createReaderConfiguration(
			String readerConfigurationFactoryID,
			AttributeList readerConfigurationProperties) throws RemoteException {

		// get the reader configuration factory that corresponds to the ID
		AbstractReaderConfigurationFactory<?> readerConfigFactory = this.readerConfigDAO
				.getReaderConfigurationFactory(readerConfigurationFactoryID);

		// Get an empty configuration
		Configuration readerConfiguration = readerConfigFactory
				.getEmptyConfiguration(readerConfigurationFactoryID);

		// set the attributes on the configuration
		readerConfiguration.setAttributes(readerConfigurationProperties);

		// create the service
		readerConfigFactory.createService(readerConfiguration);

		// return the ID
		return readerConfiguration.getServiceID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.rmi.ReaderConfigurationStub#deleteReaderConfiguration
	 * (java.lang.String)
	 */
	@Override
	public void deleteReaderConfiguration(String readerConfigurationID)
			throws RemoteException {
		Configuration config = configurationDAO
				.getConfiguration(readerConfigurationID);
		if (config != null) {
			config.destroy();
		} else {
			logger.warn("No reader configuraion found with ID: "
					+ readerConfigurationID);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.ReaderConfigurationStub#
	 * getAllReaderConfigurationProperties()
	 */
	@Override
	public Map<String, AttributeList> getAllReaderConfigurationProperties()
			throws RemoteException {
		Map<String, AttributeList> retVal = new HashMap<String, AttributeList>();

		// get all reader configurations
		Set<ReaderConfiguration<?>> configurations = this.readerConfigDAO
				.getCurrentReaderConfigurations();
		Iterator<ReaderConfiguration<?>> iter = configurations.iterator();
		while (iter.hasNext()) {
			ReaderConfiguration<?> current = iter.next();
			// get the ID of the reader configuration
			String serviceID = current.getID();
			// get the configuration object associated with the reader config
			Configuration config = this.configurationDAO
					.getConfiguration(serviceID);
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
				AttributeList attrList = config.getAttributes(names);

				retVal.put(serviceID, attrList);
			} else {
				logger.debug("Configuration with serviceID: " + serviceID
						+ " does not exist");
			}
		}
		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.ReaderConfigurationStub#
	 * getAvailableReaderConfigurationFactories()
	 */
	@Override
	public Set<String> getAvailableReaderConfigurationFactories()
			throws RemoteException {
		Set<String> retVal = new HashSet<String>();
		Set<AbstractReaderConfigurationFactory<?>> configFactories = this.readerConfigDAO
				.getCurrentReaderConfigurationFactories();
		Iterator<AbstractReaderConfigurationFactory<?>> iter = configFactories
				.iterator();
		while (iter.hasNext()) {
			AbstractReaderConfigurationFactory<?> factory = iter.next();
			retVal.add(factory.getFactoryIDs().get(0));
		}
		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.ReaderConfigurationStub#
	 * getAvailableReaderConfigurations()
	 */
	@Override
	public Map<String, String> getAvailableReaderConfigurations()
			throws RemoteException {
		Map<String, String> retVal = new HashMap<String, String>();

		// Get all ReaderConfigurations
		Set<ReaderConfiguration<?>> configurations = readerConfigDAO
				.getCurrentReaderConfigurations();
		Iterator<ReaderConfiguration<?>> iter = configurations.iterator();
		while (iter.hasNext()) {

			ReaderConfiguration<?> readerConfig = iter.next();
			// get the ID of the reader configuration
			String configID = readerConfig.getID();

			// look up the associated service configuration object for the
			// reader configuration
			Configuration config = configurationDAO.getConfiguration(configID);
			if (config != null) {
				retVal.put(configID, config.getFactoryID());
			} else {
				logger.debug("No Configuration Object with ID " + configID
						+ " is available");
			}
		}

		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.ReaderConfigurationStub#
	 * getReaderConfigurationDescription(java.lang.String)
	 */
	@Override
	public MBeanInfo getReaderConfigurationDescription(
			String readerConfigurationFactoryID) throws RemoteException {
		AbstractReaderConfigurationFactory<?> configFactory = readerConfigDAO
				.getReaderConfigurationFactory(readerConfigurationFactoryID);
		if (configFactory != null) {
			Configuration config = configFactory
					.getEmptyConfiguration(readerConfigurationFactoryID);
			return config.getMBeanInfo();
		} else {
			logger.warn("No Reader Configuration Factory with ID "
					+ readerConfigurationFactoryID + " is available");
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.ReaderConfigurationStub#
	 * getReaderConfigurationProperties(java.lang.String)
	 */
	@Override
	public AttributeList getReaderConfigurationProperties(
			String readerConfigurationID) throws RemoteException {
		Configuration config = configurationDAO
				.getConfiguration(readerConfigurationID);
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
					+ readerConfigurationID + " is available");
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.ReaderConfigurationStub#
	 * setReaderConfigurationProperties(java.lang.String,
	 * javax.management.AttributeList)
	 */
	@Override
	public AttributeList setReaderConfigurationProperties(
			String readerConfigurationID,
			AttributeList readerConfigurationProperties) throws RemoteException {
		Configuration config = configurationDAO
				.getConfiguration(readerConfigurationID);
		if (config != null) {
			return config.setAttributes(readerConfigurationProperties);
		} else {
			logger.warn("No Configuration object with ID "
					+ readerConfigurationID + " is available");
		}
		return null;
	}

	/**
	 * Used by spring to set the ReaderConfigDAO
	 * 
	 * @param readerConfigDAO
	 *            the readerConfigDAO to set
	 */
	public void setReaderConfigDAO(ReaderConfigurationDAO readerConfigDAO) {
		this.readerConfigDAO = readerConfigDAO;
	}

	/**
	 * @param configurationDAO
	 *            the configurationDAO to set
	 */
	public void setConfigurationDAO(ConfigurationDAO configurationDAO) {
		this.configurationDAO = configurationDAO;
	}

}
