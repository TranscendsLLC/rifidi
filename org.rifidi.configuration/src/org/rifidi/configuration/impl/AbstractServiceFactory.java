/**
 * 
 */
package org.rifidi.configuration.impl;
//TODO: Comments
import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.rifidi.configuration.Configuration;
import org.rifidi.configuration.RifidiService;
import org.rifidi.configuration.ServiceFactory;

/**
 * Base class for a service factory. This class is meant for scenarios where
 * there is one service class that can exist in several configurations.
 * Overwrite the factoryID and clazz attributes! The service gets registered
 * using a generated name of the form <factoryid>-<counter>.
 * 
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public abstract class AbstractServiceFactory<T extends RifidiService>
		implements ServiceFactory {
	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(AbstractServiceFactory.class);
	/** Counter for service ids. */
	private int counter = 0;
	/** Context of the registering bundle. */
	private BundleContext context;
	/** Reference to the configuration */
	private DefaultConfigurationImpl configuration = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.configuration.ServiceFactory#getEmptyConfiguration(java.lang
	 * .String)
	 */
	@Override
	public Configuration getEmptyConfiguration(String factoryID) {
		assert (getFactoryIDs().get(0).equals(factoryID));
		if (configuration == null) {
			configuration = new DefaultConfigurationImpl(getClazz(),
					getFactoryIDs().get(0));
		}
		return (Configuration) configuration.clone();
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
		assert (getFactoryIDs().get(0) != null);
		try {
			T instance = getClazz().newInstance();
			((DefaultConfigurationImpl) configuration).setTarget(instance);
			counter++;
			if (configuration.getServiceID() == null) {
				// TODO: baaad, we are depending on a concrete implementation!!!
				((DefaultConfigurationImpl) configuration)
						.setServiceID(getFactoryIDs().get(0) + "-"
								+ Integer.toString(counter));
			} else {
				String[] splitString = configuration.getServiceID().split("-");
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
			params.put("type", getClazz().getName());
			// NOTE: it is important for customInit to happen before registering
			// the service!
			customConfig(instance);
			configuration.setServiceRegistration(context.registerService(Configuration.class.getName(),
					configuration, params));
		} catch (InstantiationException e) {
			logger.error(getClazz() + " cannot be instantiated. " + e);
		} catch (IllegalAccessException e) {
			logger.error(getClazz() + " cannot be instantiated. " + e);
		}
	}

	public abstract void customConfig(T instance);

	/**
	 * Get the class this factory constructs.
	 * 
	 * @return
	 */
	public abstract Class<T> getClazz();

	/**
	 * @param context
	 *            the context to set
	 */
	public void setContext(BundleContext context) {
		this.context = context;
	}

	/**
	 * Get the bundel context for this factory.
	 * 
	 * @return
	 */
	protected BundleContext getContext() {
		return context;
	}
}
