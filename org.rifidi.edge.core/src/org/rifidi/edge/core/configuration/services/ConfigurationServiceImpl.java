package org.rifidi.edge.core.configuration.services;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanAttributeInfo;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;
import org.rifidi.edge.api.rmi.dto.SessionDTO;
import org.rifidi.edge.core.configuration.Configuration;
import org.rifidi.edge.core.configuration.ConfigurationStore;
import org.rifidi.edge.core.configuration.Constants;
import org.rifidi.edge.core.configuration.RifidiService;
import org.rifidi.edge.core.configuration.ServiceFactory;
import org.rifidi.edge.core.configuration.ServiceStore;
import org.rifidi.edge.core.configuration.impl.DefaultConfigurationImpl;
import org.rifidi.edge.core.configuration.mbeans.ConfigurationControlMBean;
import org.rifidi.edge.core.sensors.SensorSession;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.services.notification.NotifierService;

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
	private final String path;
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
	public ConfigurationServiceImpl(BundleContext context, String path,
			NotifierService notifierService, JMXService jmxService) {
		this.jmxService = jmxService;
		this.notifierService = notifierService;
		this.path = path;
		this.context = context;
		factories = new ConcurrentHashMap<String, ServiceFactory<?>>();
		IDToConfigurations = new ConcurrentHashMap<String, DefaultConfigurationImpl>();
		serviceNames = new ArrayList<String>();
		configToRegsitration = new ConcurrentHashMap<Configuration, ServiceRegistration>();
		JAXBContext jcontext = null;
		try {
			jcontext = JAXBContext.newInstance(ConfigurationStore.class,
					ServiceStore.class);
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
			store = (ConfigurationStore) jaxbContext.createUnmarshaller()
					.unmarshal(new File(path));
		} catch (JAXBException e) {
			logger.error(e);
			return ret;
		}
		if(store.getServices()!=null){
			for (ServiceStore service : store.getServices()) {
				if (ret.get(service.getFactoryID()) == null) {
					ret.put(service.getFactoryID(),
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
					attributes.add(new Attribute(key, service.getAttributes().get(
							key)));
				}
				if(!checkName(service.getServiceID())){
					continue;
				}
				ret.get(service.getFactoryID()).add(
						createAndRegisterConfiguration(service.getServiceID(),
								service.getFactoryID(), attributes));
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
			String serviceID, String factoryID, AttributeList attributes) {
		DefaultConfigurationImpl config = new DefaultConfigurationImpl(
				serviceID, factoryID, attributes, notifierService, jmxService);
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
	private boolean checkName(String configurationName) {
		String regex = "([A-Za-z0-9_])+";
		return configurationName.matches(regex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.configuration.ConfigurationService#storeConfiguration
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
			for (MBeanAttributeInfo attrInfo:config.getMBeanInfo().getAttributes()){
				if(attrInfo.isWritable()){
					attributes.put(attrInfo.getName(), configAttrs.get(attrInfo.getName()).toString());
				}
			}
			serviceStore.setAttributes(attributes);
			try{
				RifidiService target=config.getTarget();
				if(target!=null && target instanceof AbstractSensor<?>){
					serviceStore.setSessionDTOs(new HashSet<SessionDTO>());
					for(SensorSession session:((AbstractSensor<?>)target).getSensorSessions().values()){
						serviceStore.getSessionDTOs().add(session.getDTO());
					}
				}
			}catch(RuntimeException e){
				logger.info("Target went away while trying to store it: "+e);
			}
			store.getServices().add(serviceStore);
		}
		
		try {
			jaxbContext.createMarshaller().marshal(store, new File(path));
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
	public void createService(String factoryID, AttributeList attributes) {
		ServiceFactory<?> factory = factories.get(factoryID);
		if (factory == null) {
			logger.warn("Tried to use a nonexistent factory: " + factoryID);
			return;
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
				serviceID, factoryID, attributes);

		if (factoryToConfigurations.get(factoryID) == null) {
			factoryToConfigurations.put(factoryID,
					new CopyOnWriteArraySet<DefaultConfigurationImpl>());
		}
		factoryToConfigurations.get(factoryID).add(config);
		IDToConfigurations.put(serviceID, config);

		if (factory != null) {
			factory.createInstance(factoryID, serviceID);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.configuration.services.ConfigurationService#
	 * destroyService (java.lang.String)
	 */
	@Override
	public void destroyService(String serviceID) {
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
	public Configuration getConfiguration(String serviceID) {
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
	 * org.rifidi.edge.core.configuration.mbeans.ConfigurationControlMBean#reload
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
	 * org.rifidi.edge.core.configuration.mbeans.ConfigurationControlMBean#save
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
	public void setServiceFactories(Set<ServiceFactory<?>> serviceFactories) {
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
	public void bind(ServiceFactory<?> factory, Map<?, ?> properties) {
		synchronized (factories) {
			for (String factoryID : factory.getFactoryIDs()) {
				if (factories.get(factoryID) == null) {
					logger.debug("Registering " + factoryID);
					factories.put(factoryID, factory);
					if (factoryToConfigurations.get(factoryID) != null) {
						for (Configuration serConf : factoryToConfigurations
								.get(factoryID)) {
							factory.createInstance(factoryID, serConf
									.getServiceID());
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
	public synchronized void unbind(ServiceFactory<?> factory,
			Map<?, ?> properties) {
		synchronized (factories) {
			for (String factoryID : factory.getFactoryIDs()) {
				logger.debug("Unregistering " + factoryID);
				factories.remove(factoryID);
			}
		}

	}

}
