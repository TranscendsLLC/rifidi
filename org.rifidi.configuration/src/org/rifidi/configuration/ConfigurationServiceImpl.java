/**
 * 
 */
package org.rifidi.configuration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.tree.DefaultConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class ConfigurationServiceImpl implements ConfigurationService {
	/** Logger for this class */
	private static final Log logger = LogFactory
			.getLog(ConfigurationServiceImpl.class);
	/** Path to the configfile. */
	private String path;
	/** Configurations. */
	private Map<String, Set<ServiceConfiguration>> factoryToConfigurations;
	/** Currently registered services. */
	private Set<Configuration> configurations;

	/**
	 * Currently available factories by their names.
	 */
	private Map<String, ServiceFactory> factories;

	/**
	 * Constructor.
	 */
	public ConfigurationServiceImpl() {
		path = System.getProperty("org.rifidi.edge.configuration");
		factoryToConfigurations = new HashMap<String, Set<ServiceConfiguration>>(
				loadConfig());
		factories = new HashMap<String, ServiceFactory>();
		configurations = new HashSet<Configuration>();
		logger.debug("ConfigurationServiceImpl instantiated.");
	}

	/**
	 * Load the configuration.
	 * 
	 * @return
	 */
	private Map<String, Set<ServiceConfiguration>> loadConfig() {
		Map<String, Set<ServiceConfiguration>> ret = new HashMap<String, Set<ServiceConfiguration>>();
		try {
			HierarchicalINIConfiguration configuration = new HierarchicalINIConfiguration(
					path);
			// loop over configs
			for (Object sectionName : configuration.getSections()) {
				SubnodeConfiguration section = configuration
						.getSection((String) sectionName);
				String factoryName = section.getString(Constants.FACTORYID);
				// check if we have a type info
				if (factoryName == null) {
					logger.fatal("Missing factoryid attribute in config of "
							+ sectionName);
					continue;
				}
				if (ret.get(factoryName) == null) {
					ret.put(factoryName, new HashSet<ServiceConfiguration>());
				}

				HashMap<String, String> configurationDictionary = new HashMap<String, String>();
				// get all properties
				Iterator<String> keys = section.getKeys();
				while (keys.hasNext()) {
					String key = keys.next();
					// factoryid is already processed
					if (Constants.FACTORYID.equals(key)) {
						continue;
					}
					configurationDictionary.put(key, section.getString(key));
				}

				ServiceConfiguration config = new ServiceConfiguration();
				config.properties = configurationDictionary;
				config.serviceID = (String) sectionName;
				logger.debug("Read configuration for " + config.serviceID
						+ " (" + factoryName + ")");
				ret.get(factoryName).add(config);
			}
		} catch (ConfigurationException e) {
			logger.fatal("Can't open configuration: " + e);
		}
		return ret;
	}

	/**
	 * The given service was bound to the registry.
	 * 
	 * @param serviceRef
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void bind(ServiceFactory factory, Map<?, ?> properties) {
		synchronized (factories) {
			if (factories.get(factory.getFactoryID()) == null) {
				logger.debug("Registering " + factory.getFactoryID());
				factories.put(factory.getFactoryID(), factory);
				for (ServiceConfiguration serConf : factoryToConfigurations
						.get(factory.getFactoryID())) {
					factory
							.createService(serConf.serviceID,
									serConf.properties);
				}
			}
		}
	}

	/**
	 * The given service has been removed.
	 * 
	 * @param serviceRef
	 * @throws Exception
	 */
	public synchronized void unbind(ServiceFactory factory, Map<?, ?> properties) {
		synchronized (factories) {
			logger.debug("Unregistering " + factory.getFactoryID());
			factories.remove(factory.getFactoryID());
		}

	}

	/**
	 * Called whenever a new Configuration got registered to OSGi.
	 * 
	 * @param config
	 * @param properties
	 */
	public void register(Configuration config, Map<?, ?> properties) {
		configurations.add(config);
	}

	/**
	 * Called whenever a Configuration got removed from OSGi.
	 * 
	 * @param config
	 * @param properties
	 */
	public void unregister(Configuration config, Map<?, ?> properties) {
		configurations.remove(config);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.configuration.ConfigurationService#storeConfiguration()
	 */
	@Override
	public synchronized void storeConfiguration() {
		HashSet<Configuration> copy = new HashSet<Configuration>(configurations);
		try {
			//TODO: copy file before deleting it!!
			File file=new File(path);
			file.delete();
			file.createNewFile();
			HierarchicalINIConfiguration configuration = new HierarchicalINIConfiguration(
					path);
			configuration.clear();
			for (Configuration config : copy) {
				DefaultConfigurationNode section = new DefaultConfigurationNode(
						config.getServiceID());
				DefaultConfigurationNode type = new DefaultConfigurationNode(
						Constants.FACTORYID);
				type.setValue(config.getFactoryID());
				section.addChild(type);
				Map<String, String> attrs = config.getAttributes();
				for (String key : attrs.keySet()) {
					DefaultConfigurationNode newNode = new DefaultConfigurationNode(
							(String) key);
					newNode.setValue(attrs.get(key));
					section.addChild(newNode);
				}
				configuration.getRootNode().addChild(section);
			}

			configuration.save();
		} catch (ConfigurationException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	/**
	 * Set the current service factories.
	 * 
	 * @param serviceFactories
	 */
	public void setServiceFactories(Set<ServiceFactory> serviceFactories) {
		for (ServiceFactory serviceFactory : serviceFactories) {
			bind(serviceFactory, null);
		}
	}

	/**
	 * @param configurations
	 *            the configurations to set
	 */
	public void setConfigurations(Set<Configuration> configurations) {
		for (Configuration config : configurations) {
			register(config, null);
		}
	}

	/**
	 * Just a container for service configurations.
	 * 
	 * @author Jochen Mader - jochen@pramari.com
	 * 
	 */
	protected class ServiceConfiguration {
		public HashMap<String, String> properties;
		public String serviceID;
	}
}
