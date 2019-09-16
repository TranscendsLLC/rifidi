/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
package org.rifidi.edge.configuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanAttributeInfo;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;
import org.rifidi.edge.api.SessionDTO;
import org.rifidi.edge.exceptions.CannotCreateServiceException;
import org.rifidi.edge.exceptions.InvalidStateException;
import org.rifidi.edge.notification.NotifierService;
import org.rifidi.edge.sensors.AbstractSensor;
import org.rifidi.edge.sensors.SensorSession;
import org.springframework.core.io.Resource;

/**
 * Implementation of service for managing configurations.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class ConfigurationServiceImpl implements ConfigurationService,
		ConfigurationControlMBean {
	/** Logger for this class */
	private static final Log logger = LogFactory
			.getLog(ConfigurationServiceImpl.class);
	/** Path to the configfile. */
	private final Resource persistanceResource;
	/** Configurations. */
	private final ConcurrentHashMap<String, Set<DefaultConfigurationImpl>> factoryToConfigurations;
	/** Currently registered services. */
	private final ConcurrentHashMap<String, DefaultConfigurationImpl> IDToConfigurations;
	/** Configurations with their registration objects. */
	private final ConcurrentHashMap<Configuration, ServiceRegistration> configToRegsitration;
	/** Service names that are already taken. */
	private final List<String> serviceNames;
	/** A notifier for JMS. Remove once we have aspects */
	private volatile NotifierService notifierService;
	/**
	 * Currently available factories by their names.
	 */
	private final Map<String, ServiceFactory<?>> factories;
	/** Contex for the bundle we are running in. */
	private final BundleContext context;
	/** JMXservice reference. */
	private volatile JMXService jmxService;
	/** JAXB context. */
	private final JAXBContext jaxbContext;

	/**
	 * Constructor.
	 */
	public ConfigurationServiceImpl(final BundleContext context,
			final Resource persistanceResource, final NotifierService notifierService,
			final JMXService jmxService) {
		this.jmxService = jmxService;
		this.notifierService = notifierService;
		this.persistanceResource = persistanceResource;
		this.context = context;
		factories = new ConcurrentHashMap<String, ServiceFactory<?>>();
		IDToConfigurations = new ConcurrentHashMap<String, DefaultConfigurationImpl>();
		serviceNames = new ArrayList<String>();
		configToRegsitration = new ConcurrentHashMap<Configuration, ServiceRegistration>();
		JAXBContext jcontext = null;
		try {
			jcontext = JAXBContext.newInstance(ConfigurationStore.class,ServiceStore.class);
		} catch (JAXBException e) {
			logger.error(e);
		}
		jaxbContext = jcontext;

		factoryToConfigurations = loadConfig();
		// TODO: review and reenable
		// jmxService.setConfigurationControlMBean(this);
		logger.debug("ConfigurationServiceImpl instantiated.");
	}

	/**
	 * Load the configuration. Not thread safe.
	 * 
	 * @return
	 */
	private ConcurrentHashMap<String, Set<DefaultConfigurationImpl>> loadConfig() {
		ConcurrentHashMap<String, Set<DefaultConfigurationImpl>> ret = new ConcurrentHashMap<String, Set<DefaultConfigurationImpl>>();

		ConfigurationStore store;
		try {
			store = (ConfigurationStore) jaxbContext.createUnmarshaller().unmarshal(persistanceResource.getFile());
		} catch (IOException e) {
			logger.error("Error loading config/rifidi.xml, no configuration loaded");
			return ret;
		} catch (JAXBException e) {
			logger.error("Exception loading config/rifidi.xml or file not found, no configuration loaded");
			return ret;
		}
		if (store.getServices() != null) {
			for (ServiceStore service : store.getServices()) {
				if (ret.get(service.getFactoryID()) == null) {
					ret
							.put(
									service.getFactoryID(),
									new CopyOnWriteArraySet<DefaultConfigurationImpl>());
				}
				AttributeList attributes = new AttributeList();
				// get all properties
				for (String key : service.getAttributes().keySet()) {
					// factoryid is already processed
					if (Constants.FACTORYID.equals(key)) {
						continue;
					}
					// type is already processed
					if (Constants.FACTORY_TYPE.equals(key)) {
						continue;
					}
					attributes.add(new Attribute(key, service.getAttributes()
							.get(key)));
				}
				if (!checkName(service.getServiceID())) {
					continue;
				}
				ret.get(service.getFactoryID()).add(
						createAndRegisterConfiguration(service.getServiceID(),
								service.getFactoryID(), attributes, service
										.getSessionDTOs()));
				serviceNames.add(service.getServiceID());
			}
		}
		return ret;
	}

	/**
	 * Helper for creating a configuration and registering it to jms.
	 * 
	 * @param serviceID
	 * @param factoryID
	 * @param attributes
	 * @return
	 */
	private DefaultConfigurationImpl createAndRegisterConfiguration(
			final String serviceID, final String factoryID,
			final AttributeList attributes, final Set<SessionDTO> sessionDTOs) {
		DefaultConfigurationImpl config = new DefaultConfigurationImpl(
				serviceID, factoryID, attributes, notifierService, jmxService,
				sessionDTOs);
		config.setContext(context);
		IDToConfigurations.put(serviceID, config);

		String[] serviceInterfaces = new String[] { Configuration.class
				.getCanonicalName() };
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("serviceid", config.getServiceID());
		configToRegsitration.put(config, context.registerService(
				serviceInterfaces, config, params));

		try {
			context.addServiceListener(config, "(serviceid="
					+ config.getServiceID() + ")");
			logger.debug("Added listener for (serviceid="
					+ config.getServiceID() + ")");
		} catch (InvalidSyntaxException e) {
			logger.fatal(e);
		}
		return config;
	}

	/**
	 * Configuration names may only consist of alpha-numeric characters and the
	 * underscore character because of esper.
	 * 
	 * @param configurationName
	 *            The name of a configuration that is read in
	 * @return true if the configuration name passes the check. False otherwise.
	 */
	private boolean checkName(final String configurationName) {
		String regex = "([A-Za-z0-9_])+";
		return configurationName.matches(regex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.configuration.ConfigurationService#storeConfiguration
	 * ()
	 */
	@Override
	public synchronized void storeConfiguration() {
		HashSet<DefaultConfigurationImpl> copy = new HashSet<DefaultConfigurationImpl>(
				IDToConfigurations.values());
		ConfigurationStore store = new ConfigurationStore();
		store.setServices(new ArrayList<ServiceStore>());
		for (DefaultConfigurationImpl config : copy) {
			ServiceStore serviceStore = new ServiceStore();
			serviceStore.setServiceID(config.getServiceID());
			serviceStore.setFactoryID(config.getFactoryID());

			Map<String, Object> configAttrs = config.getAttributes();
			Map<String, String> attributes = new HashMap<String, String>();
			try {
				for (MBeanAttributeInfo attrInfo : config.getMBeanInfo()
						.getAttributes()) {
					if (attrInfo.isWritable()) {
						try {
							attributes.put(attrInfo.getName(), configAttrs.get(
									attrInfo.getName()).toString());
						} catch (NullPointerException ex) {
							logger.error("No property: " + attrInfo.getName());
						}
					}
				}
			} catch (NullPointerException ex) {
				logger.error("Problem with config: " + config);
			}
			serviceStore.setAttributes(attributes);
			try {
				RifidiService target = config.getTarget();
				if (target != null && target instanceof AbstractSensor<?>) {
					serviceStore.setSessionDTOs(new HashSet<SessionDTO>());
					for (SensorSession session : ((AbstractSensor<?>) target)
							.getSensorSessions().values()) {
						serviceStore.getSessionDTOs().add(session.getDTO());
					}
				}
			} catch (RuntimeException e) {
				logger.warn("Target went away while trying to store it: " + e);
			}
			store.getServices().add(serviceStore);
		}

		try {
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			File file = persistanceResource.getFile();
			marshaller.marshal(store, file);
			logger.info("configuration saved at " + file);
		}catch(IOException e){
			logger.error(e);
		} catch (JAXBException e) {
			logger.error(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.configuration.services.ConfigurationService#
	 * createService( java.lang.String, javax.management.AttributeList)
	 */
	@Override
	public String createService(final String factoryID,
			final AttributeList attributes) throws CannotCreateServiceException {
		ServiceFactory<?> factory = factories.get(factoryID);
		if (factory == null) {
			logger.warn("Tried to use a nonexistent factory: " + factoryID);
			throw new CannotCreateServiceException();
		}
		String serviceID = factoryID;
		serviceID = serviceID.replaceAll("[^A-Z^a-z^0-9^_]", "_") + "_";
		synchronized (serviceNames) {
			Integer counter = 1;
			String tempServiceID = serviceID + 1;
			// TODO: not nice but good enough for now
			while (serviceNames.contains(tempServiceID)) {
				counter++;
				tempServiceID = serviceID + counter;
			}
			serviceID = tempServiceID;
			serviceNames.add(serviceID);
		}

		DefaultConfigurationImpl config = createAndRegisterConfiguration(
				serviceID, factoryID, attributes, new HashSet<SessionDTO>());

		if (factoryToConfigurations.get(factoryID) == null) {
			factoryToConfigurations.put(factoryID,
					new CopyOnWriteArraySet<DefaultConfigurationImpl>());
		}
		factoryToConfigurations.get(factoryID).add(config);
		IDToConfigurations.put(serviceID, config);

		if (factory != null) {
			// TODO: Ticket #236
			try {
				factory.createInstance(serviceID);
				return serviceID;
			} catch (IllegalArgumentException e) {
				logger.error("exception", e);
			} catch (InvalidStateException e) {
				logger.error("exception ", e);
			}
		}
		throw new CannotCreateServiceException();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.configuration.services.ConfigurationService#
	 * createService( java.lang.String, javax.management.AttributeList, java.lang.String)
	 */
	@Override
	public String createService(final String factoryID,
			final AttributeList attributes, String requiredServiceID) throws Exception {
		ServiceFactory<?> factory = factories.get(factoryID);
		if (factory == null) {
			logger.warn("Tried to use a nonexistent factory: " + factoryID);
			throw new CannotCreateServiceException();
		}

		//Validate and accept only alphanumeric values, colons, periods, hyphens and underscores
		Pattern pattern = Pattern.compile("^[a-zA-Z0-9_:.\\-]+$");
		Matcher matcher = pattern.matcher(requiredServiceID);
		if (!matcher.matches()){
			throw new CannotCreateServiceException("Invalid character for reader id. It's allowed only alphanumeric and underscore characters");
		}
 
		synchronized (serviceNames) {

			if (serviceNames.contains(requiredServiceID)) {
				throw new CannotCreateServiceException("Service with id: " + requiredServiceID + " already exists");
			}
			serviceNames.add(requiredServiceID);
		}

		DefaultConfigurationImpl config = createAndRegisterConfiguration(
				requiredServiceID, factoryID, attributes, new HashSet<SessionDTO>());

		if (factoryToConfigurations.get(factoryID) == null) {
			factoryToConfigurations.put(factoryID,
					new CopyOnWriteArraySet<DefaultConfigurationImpl>());
		}
		factoryToConfigurations.get(factoryID).add(config);
		IDToConfigurations.put(requiredServiceID, config);

		if (factory != null) {
			// TODO: Ticket #236
			try {
				factory.createInstance(requiredServiceID);
				return requiredServiceID;
			} catch (IllegalArgumentException e) {
				logger.error("exception", e);
			} catch (InvalidStateException e) {
				logger.error("exception ", e);
			}
		}
		throw new CannotCreateServiceException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.configuration.services.ConfigurationService#
	 * destroyService (java.lang.String)
	 */
	@Override
	public void destroyService(final String serviceID) {
		synchronized (serviceNames) {
			serviceNames.remove(serviceID);
			DefaultConfigurationImpl config = IDToConfigurations
					.remove(serviceID);
			factoryToConfigurations.get(config.getFactoryID()).remove(config);
			context.removeServiceListener(config);
			configToRegsitration.remove(config).unregister();
			config.destroy();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.configuration.services.ConfigurationService#
	 * getConfiguration (java.lang.String)
	 */
	@Override
	public Configuration getConfiguration(final String serviceID) {
		return IDToConfigurations.get(serviceID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.configuration.services.ConfigurationService#
	 * getConfigurations ()
	 */
	@Override
	public Set<Configuration> getConfigurations() {
		return new HashSet<Configuration>(IDToConfigurations.values());
	}

	// Only used by Spring

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.configuration.mbeans.ConfigurationControlMBean#reload
	 * ()
	 */
	@Override
	public void reload() {
		logger.warn("Please implement reload");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.configuration.mbeans.ConfigurationControlMBean#save
	 * ()
	 */
	@Override
	public void save() {
		storeConfiguration();
	}

	/**
	 * Set the current service factories.
	 * 
	 * @param serviceFactories
	 */
	public void setServiceFactories(
			final Set<ServiceFactory<?>> serviceFactories) {
		for (ServiceFactory<?> serviceFactory : serviceFactories) {
			bind(serviceFactory, null);
		}
	}

	/**
	 * The given service was bound to the registry.
	 * 
	 * @param serviceRef
	 * @throws Exception
	 */
	public void bind(final ServiceFactory<?> factory, final Map<?, ?> properties) {
		synchronized (factories) {
			if (factories.get(factory.getFactoryID()) == null) {
				logger.debug("Registering " + factory.getFactoryID());
				factories.put(factory.getFactoryID(), factory);
				if (factoryToConfigurations.get(factory.getFactoryID()) != null) {
					for (Configuration serConf : factoryToConfigurations
							.get(factory.getFactoryID())) {

						// TODO: Ticket #236
						try {
							factory.createInstance(serConf.getServiceID());
						} catch (IllegalArgumentException e) {
							logger.error("exception", e);
						} catch (InvalidStateException e) {
							logger.error("exception ", e);
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
	public synchronized void unbind(final ServiceFactory<?> factory,
			Map<?, ?> properties) {
		synchronized (factories) {
			factories.remove(factory.getFactoryID());
		}

	}

}
