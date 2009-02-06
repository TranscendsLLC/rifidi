/**
 * 
 */
package org.rifidi.configuration;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.tree.DefaultConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

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
	/** Reference to the OSGi configuration admin service. */
	private ConfigurationAdmin configAdmin;
	/** Map of PIDs to their bundle locations. */
	private Map<String, String> locations;
	/** Bundle context of the owning bundle. */
	private BundleContext context;
	/** Configurations. */
	private Map<String, Set<ServiceConfiguration>> factoryToConfigurations;

	/**
	 * Constructor.
	 */
	public ConfigurationServiceImpl() {
		locations = new HashMap<String, String>();
		factoryToConfigurations = new HashMap<String, Set<ServiceConfiguration>>();
		path = System.getProperty("org.rifidi.edge.configuration");
		logger.debug("ConfigurationServiceImpl instantiated.");
		factoryToConfigurations = loadConfig();
	}

	/**
	 * Instantiate all the configurations associated with the given factory
	 * name.
	 * 
	 * @param factory
	 */
	private void initFactory(String factory) {
		for (ServiceConfiguration configuration : factoryToConfigurations
				.get(factory)) {
			try {
				configuration.configuration = configAdmin
						.createFactoryConfiguration(factory);
				configuration.configuration.setBundleLocation(locations
						.get(factory));
				configuration.configuration.update(configuration.properties);
				logger.debug("Created service " + configuration.name);
			} catch (IOException e) {
				logger.error(e);
			}
		}
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
				String factoryName = section.getString("type");
				if (ret.get(factoryName) == null) {
					ret.put(factoryName, new HashSet<ServiceConfiguration>());
				}
				// check if we have a type info
				if (factoryName == null) {
					logger.fatal("Missing type atribute in config of "
							+ sectionName);
					continue;
				}

				Dictionary<String, String> configurationDictionary = new Hashtable<String, String>();
				// get all properties
				Iterator<String> keys = section.getKeys();
				while (keys.hasNext()) {
					String key = keys.next();
					if ("type".equals(key))
						continue;
					configurationDictionary.put(key, section.getString(key));
				}
				configurationDictionary.put("name", (String) sectionName);
				ServiceConfiguration config = new ServiceConfiguration();
				config.properties = configurationDictionary;
				config.name = (String) sectionName;
				logger.debug("Read configuration for " + config.name + " ("
						+ factoryName + ")");
				ret.get(factoryName).add(config);
			}
		} catch (ConfigurationException e) {
			logger.fatal("Can't open configuration: " + e);
		}
		return ret;
	}

	/**
	 * Set reference to the configuration admin.
	 * 
	 * @param configAdmin
	 */
	public void setConfigurationAdminService(ConfigurationAdmin configAdmin) {
		logger.debug("Got configuration admin instance: " + configAdmin);
		this.configAdmin = configAdmin;
	}

	/**
	 * The given service was bound to the registry.
	 * 
	 * @param serviceRef
	 * @throws Exception
	 */
	public void bind(ServiceReference serviceRef) throws Exception {
		// add to the list of available configurations
		locations.put((String) serviceRef.getProperty(Constants.SERVICE_PID),
				serviceRef.getBundle().getLocation());

		if ("true".equals(System.getProperty("osgi.clean"))) {
			initFactory((String) serviceRef.getProperty(Constants.SERVICE_PID));
		}
	}

	/**
	 * The given service has been removed.
	 * 
	 * @param serviceRef
	 * @throws Exception
	 */
	public void unbind(ServiceReference serviceRef) throws Exception {
		locations
				.remove((String) serviceRef.getProperty(Constants.SERVICE_PID));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.configuration.ConfigurationService#storeConfiguration()
	 */
	@Override
	public void storeConfiguration() {
		try {
			HierarchicalINIConfiguration configuration = new HierarchicalINIConfiguration(
					path);
			configuration.clear();
			for(Configuration config:configAdmin.listConfigurations(null)){
				DefaultConfigurationNode section=new DefaultConfigurationNode((String)config.getProperties().get("name"));
				DefaultConfigurationNode type=new DefaultConfigurationNode("type");
				type.setValue(config.getFactoryPid());
				section.addChild(type);
				Enumeration<?> keys=config.getProperties().keys();
				while(keys.hasMoreElements()){
					Object key=keys.nextElement();
					if(!"name".equals(key) && !((String)key).startsWith("service")){
						DefaultConfigurationNode newNode=new DefaultConfigurationNode((String)key);
						newNode.setValue(config.getProperties().get(key));
						section.addChild(newNode);
					}
				}
				configuration.getRootNode().addChild(section);
			}
			
			
			configuration.save();
		} catch (ConfigurationException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		} catch (InvalidSyntaxException e) {
			logger.error(e);
		}
	}

	/**
	 * Just a container for service configurations.
	 * 
	 * @author Jochen Mader - jochen@pramari.com
	 * 
	 */
	protected class ServiceConfiguration {
		public Dictionary<String, String> properties;
		public Configuration configuration;
		public String name;
	}

}
