/**
 * 
 */
package org.rifidi.configuration.impl;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.rifidi.configuration.Configuration;
import org.rifidi.configuration.ServiceFactory;

/**
 * Base class for a service factory. This class is meant for scenarios where
 * there is one service class that can exist in several configurations. The
 * service gets registered using a generated name of the form
 * <factoryid>-<counter>.
 * 
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public abstract class AbstractMultiServiceFactory implements ServiceFactory {
	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(AbstractMultiServiceFactory.class);
	/** Counter for service ids. */
	private int counter = 0;
	/** Context of the registering bundle. */
	private BundleContext context;
	/** Chache for configs that have already benn processed. */
	private Map<String, DefaultConfigurationImpl> factoryIDToConfig;

	/**
	 * Constructor
	 */
	public AbstractMultiServiceFactory() {
		factoryIDToConfig = new HashMap<String, DefaultConfigurationImpl>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.configuration.ServiceFactory#getEmptyConfiguration(java.lang
	 * .String)
	 */
	@Override
	public Configuration getEmptyConfiguration(String factoryID) {
		if (!factoryIDToConfig.containsKey(factoryID)) {
			factoryIDToConfig.put(factoryID, new DefaultConfigurationImpl(
					getFactoryIDToClass().get(factoryID), factoryID));
		}
		return (Configuration) factoryIDToConfig.get(factoryID).clone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.configuration.ServiceFactory#createService(java.lang.String,
	 * java.util.Map)
	 */
	@Override
	public synchronized void createService(Configuration configuration) {
		try {
			Object instance = getFactoryIDToClass().get(
					configuration.getFactoryID()).newInstance();
			counter++;
			((DefaultConfigurationImpl) configuration).setTarget(instance);
			if (configuration.getServiceID() == null) {
				// serviceids can only have alphanumeric characters and _ in
				// them.
				String serviceID = configuration.getFactoryID();
				serviceID = serviceID.replaceAll("[^A-Z^a-z^0-9^_]", "_");
				serviceID = serviceID + "_" + Integer.toString(counter);
				((DefaultConfigurationImpl) configuration)
						.setServiceID(serviceID);
			} else {
				String[] splitString = configuration.getServiceID().split("_");
				if (splitString.length > 0) {
					String idNumString = splitString[splitString.length - 1];
					try {
						int idNum = Integer.parseInt(idNumString);
						if (counter < idNum) {
							counter = idNum;
						}
					} catch (NumberFormatException e) {
						logger.debug("Unable to parse service id: "
								+ configuration.getServiceID());
					}
				}
			}

			Dictionary<String, String> params = new Hashtable<String, String>();
			params.put("type", getFactoryIDToClass().get(
					configuration.getFactoryID()).getName());
			// NOTE: it is important for customInit to happen before registering
			// the service!
			customInit(instance);
			configuration.setServiceRegistration(context.registerService(
					Configuration.class.getName(), configuration, params));
		} catch (InstantiationException e) {
			logger.error(getFactoryIDToClass()
					.get(configuration.getFactoryID())
					+ " cannot be instantiated. " + e);
		} catch (IllegalAccessException e) {
			logger.error(getFactoryIDToClass()
					.get(configuration.getFactoryID())
					+ " cannot be instantiated. " + e);
		}
	}

	/**
	 * This is a hook for initializing the service (e.g. registering it in osgi
	 * registry under a custom interface). Called before the Configuration
	 * wrapper around this service is registered as a Configuration object in
	 * the OSGi registry
	 * 
	 * @param instance
	 *            The instance of the service
	 */
	public abstract void customInit(Object instance);

	/**
	 * A map containing the factoryids as key and the class that the factoryid
	 * should produce as value.
	 * 
	 * @return
	 */
	public abstract Map<String, Class<?>> getFactoryIDToClass();

	/**
	 * @param context
	 *            the context to set
	 */
	public void setContext(BundleContext context) {
		this.context = context;
	}

	/**
	 * Get the bundle context for this factory.
	 * 
	 * @return
	 */
	protected BundleContext getContext() {
		return context;
	}
}
