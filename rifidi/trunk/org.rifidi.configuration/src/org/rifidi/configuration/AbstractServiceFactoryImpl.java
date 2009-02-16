/**
 * 
 */
package org.rifidi.configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;

/**
 * Extend this class to create a new service factory. Overwrite the factoryID
 * and clazz attributes! The service gets registered using a generated name of
 * the form <factoryid>-<counter>.
 * 
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public abstract class AbstractServiceFactoryImpl<T> implements ServiceFactory {
	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(AbstractServiceFactoryImpl.class);
	/** Counter for service ids. */
	private int counter = 0;
	/** Context of the registering bundle. */
	private BundleContext context;
	/** Reference to the configuration */
	DefaultConfigurationImpl configuration = null;

	/* (non-Javadoc)
	 * @see org.rifidi.configuration.ServiceFactory#getEmptyConfiguration()
	 */
	@Override
	public Configuration getEmptyConfiguration() {
		if (configuration == null) {
			configuration = new DefaultConfigurationImpl(getClazz(),
					getFactoryID());
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
		assert (getFactoryID() != null);
		try {
			T instance = getClazz().newInstance();
			counter++;
			((DefaultConfigurationImpl) configuration).setTarget(instance);
			((DefaultConfigurationImpl) configuration)
					.setServiceID(getFactoryID() + "-"
							+ Integer.toString(counter));
			context.registerService(Configuration.class.getName(),
					configuration, null);
		} catch (InstantiationException e) {
			logger.error(getClazz() + " cannot be instantiated. " + e);
		} catch (IllegalAccessException e) {
			logger.error(getClazz() + " cannot be instantiated. " + e);
		}
	}

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
}
