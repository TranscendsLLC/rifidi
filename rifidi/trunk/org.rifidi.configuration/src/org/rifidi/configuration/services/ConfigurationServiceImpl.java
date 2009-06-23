package org.rifidi.configuration.services;

import java.io.File;
import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.ReflectionException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.tree.DefaultConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.configuration.Configuration;
import org.rifidi.configuration.Constants;
import org.rifidi.configuration.RifidiService;
import org.rifidi.configuration.ServiceFactory;
import org.rifidi.configuration.listeners.AttributesChangedListener;
import org.rifidi.edge.core.services.notification.NotifierService;

/**
 * Implementation of service for managing configurations.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class ConfigurationServiceImpl implements ConfigurationService,
		AttributesChangedListener {
	/** Logger for this class */
	private static final Log logger = LogFactory
			.getLog(ConfigurationServiceImpl.class);
	/** Path to the configfile. */
	private final String path;
	/** Configurations. */
	private Map<String, Set<ServiceConfiguration>> factoryToConfigurations;
	/** Currently registered services. */
	private Map<String, Configuration> IDToConfigurations;
	/** A notifier for JMS. Remove once we have aspects */
	private NotifierService notifierService;

	/**
	 * Currently available factories by their names.
	 */
	private Map<String, ServiceFactory> factories;

	/**
	 * Constructor.
	 */
	public ConfigurationServiceImpl(String path) {
		this.path = path;
		factoryToConfigurations = new HashMap<String, Set<ServiceConfiguration>>(
				loadConfig());
		factories = new HashMap<String, ServiceFactory>();
		IDToConfigurations = new HashMap<String, Configuration>();
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
				String serviceID = (String) sectionName;
				if (!checkName(serviceID)) {
					logger.fatal("service id " + serviceID
							+ " is invalid.  FactoryIDs must consist only of "
							+ "alphanumeric characters and the "
							+ "underscore character");
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
	 * Configuration names may only consist of alpha-numeric characters and the
	 * underscore character because of esper
	 * 
	 * @param configurationName
	 *            The name of a configuration that is read in
	 * @return true if the configuration name passes the check. False otherwise.
	 */
	private boolean checkName(String configurationName) {
		String regex = "([A-Za-z0-9_])+";
		return configurationName.matches(regex);
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
			for (String factoryID : factory.getFactoryIDs()) {
				if (factories.get(factoryID) == null) {
					logger.debug("Registering " + factoryID);
					factories.put(factoryID, factory);
					if (factoryToConfigurations.get(factoryID) != null) {
						for (ServiceConfiguration serConf : factoryToConfigurations
								.get(factoryID)) {
							Configuration configuration = factory
									.getEmptyConfiguration(factoryID);
							configuration.setServiceID(serConf.serviceID);
							for (String prop : serConf.properties.keySet()) {
								Attribute attribute = new Attribute(prop,
										serConf.properties.get(prop));
								try {
									configuration.setAttribute(attribute);
								} catch (AttributeNotFoundException e) {
									logger.error("Unable to set attribute: "
											+ attribute + " " + e);
								} catch (InvalidAttributeValueException e) {
									logger.error("Unable to set attribute: "
											+ attribute + " " + e);
								} catch (MBeanException e) {
									logger.error("Unable to set attribute: "
											+ attribute + " " + e);
								} catch (ReflectionException e) {
									logger.error("Unable to set attribute: "
											+ attribute + " " + e);
								}
							}
							factory.createService(configuration);
						}
					}
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
			for (String factoryID : factory.getFactoryIDs()) {
				logger.debug("Unregistering " + factoryID);
				factories.remove(factoryID);
			}
		}

	}

	/**
	 * Called whenever a new Configuration got registered to OSGi.
	 * 
	 * @param config
	 * @param properties
	 */
	public void register(Configuration config, Map<?, ?> properties) {
		IDToConfigurations.put(config.getServiceID(), config);

		// TODO: Get rid of this code once we get aspects!!!!!
		config.addAttributesChangedListener(this);
		switch (config.getType()) {
		case READER:
			notifierService.addReaderEvent(config.getServiceID());
			break;
		case COMMAND:
			notifierService.addCommandEvent(config.getServiceID());
			break;
		}
	}

	/**
	 * Called whenever a Configuration got removed from OSGi.
	 * 
	 * @param config
	 * @param properties
	 */
	public void unregister(Configuration config, Map<?, ?> properties) {
		IDToConfigurations.remove(config.getServiceID());

		// TODO: Get rid of this code once we get aspects!!!!!
		config.removeAttributesChangedListener(this);
		switch (config.getType()) {
		case READER:
			notifierService.removeReaderEvent(config.getServiceID());
			break;
		case COMMAND:
			notifierService.removeCommandEvent(config.getServiceID());
			break;
		}

	}

	@Override
	public void attributesChanged(String configurationID,
			AttributeList attributes) {
		Configuration config = IDToConfigurations.get(configurationID);
		if (config != null) {
			switch (config.getType()) {
			case READER:
				notifierService.attributesChanged(configurationID, attributes,
						true);
				break;
			case COMMAND:
				notifierService.attributesChanged(configurationID, attributes,
						false);
				break;
			}
		}

	}

	/**
	 * @param notifierService
	 *            the notifierService to set
	 */
	public void setNotifierService(NotifierService notifierService) {
		this.notifierService = notifierService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.configuration.ConfigurationService#storeConfiguration()
	 */
	@Override
	public synchronized void storeConfiguration() {
		HashSet<Configuration> copy = new HashSet<Configuration>(
				IDToConfigurations.values());
		try {
			// TODO: copy file before deleting it!!
			File file = new File(path);
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
				MBeanAttributeInfo[] infos = config.getMBeanInfo()
						.getAttributes();
				Map<String, MBeanAttributeInfo> attrInfo = new HashMap<String, MBeanAttributeInfo>();
				for (MBeanAttributeInfo i : infos) {
					attrInfo.put(i.getName(), i);
				}
				for (String key : attrs.keySet()) {
					if (attrInfo.get(key).isWritable()) {
						DefaultConfigurationNode newNode = new DefaultConfigurationNode(
								(String) key);
						newNode.setValue(attrs.get(key));
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.configuration.services.ConfigurationService#getConfiguration
	 * (java.lang.String)
	 */
	@Override
	public Configuration getConfiguration(String serviceID) {
		return IDToConfigurations.get(serviceID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.configuration.services.ConfigurationService#getConfigurations
	 * ()
	 */
	@Override
	public Set<Configuration> getConfigurations() {
		return new HashSet<Configuration>(IDToConfigurations.values());
	}

	/**
	 * Called by spring
	 * 
	 * @param IDToConfigurations
	 *            the IDToConfigurations to set
	 */
	public void setConfigurations(Set<Configuration> configurations) {
		for (Configuration config : configurations) {
			register(config, null);
		}
	}

	/**
	 * Just a container for service IDToConfigurations.
	 * 
	 * @author Jochen Mader - jochen@pramari.com
	 */
	protected class ServiceConfiguration {
		public HashMap<String, String> properties;
		public String serviceID;
	}

	/**
	 * Called by spring to register new RifidiServices
	 * 
	 * @param service
	 *            The Rifidi Server to regiter
	 * @param parameters
	 */
	public void bindRifidiService(RifidiService service,
			Dictionary<String, String> parameters) {
		logger.info("Service detected: " + service.getID());
	}

	/**
	 * Called by spring when a Rifidi Service has gone away
	 * 
	 * @param service
	 *            Service that has become unavailable
	 * @param parameters
	 */
	public void unbindRifidiService(RifidiService service,
			Dictionary<String, String> parameters) {
		logger.warn("about to call destroy");
		Configuration config = this.IDToConfigurations.get(service.getID());
		if (config != null) {
			config.destroy();
		}
	}

	/**
	 * Called by spring.
	 * 
	 * @param services
	 */
	public void setRifidiService(Set<RifidiService> services) {
		logger.info("Rifidi Services Set: " + services);
	}
}
