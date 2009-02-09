/**
 * 
 */
package org.rifidi.configuration;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.ReflectionException;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.configuration.ServiceFactory#createService(java.lang.String,
	 * java.util.Map)
	 */
	@Override
	public final void createService(String serviceID,
			Map<String, String> properties) {
		assert (getFactoryID() != null);
		try {
			T instance = getClazz().newInstance();
			DefaultConfigurationImpl config = new DefaultConfigurationImpl(
					instance, getFactoryID(), serviceID);
			for (String attributename : properties.keySet()) {
				if (Constants.FACTORYID.equals(attributename)) {
					continue;
				}
				Attribute attribute = new Attribute(attributename, properties
						.get(attributename));
				config.setAttribute(attribute);
			}

			Dictionary<String, String> params = new Hashtable<String, String>();
			params.put(Constants.SERVICEID, serviceID);
			params.put(Constants.FACTORYID, getFactoryID());
			
			context
					.registerService(Configuration.class.getName(), config,
							null);

		} catch (InstantiationException e) {
			logger.error(getClazz() + " cannot be instantiated. " + e);
		} catch (IllegalAccessException e) {
			logger.error(getClazz() + " cannot be instantiated. " + e);
		} catch (AttributeNotFoundException e) {
			logger.error("Unable to set attribute: " + e);
		} catch (InvalidAttributeValueException e) {
			logger.error("Unable to set attribute: " + e);
		} catch (MBeanException e) {
			logger.error("Unable to set attribute: " + e);
		} catch (ReflectionException e) {
			logger.error("Unable to set attribute: " + e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.configuration.ServiceFactory#createService(java.util.Map)
	 */
	@Override
	public final void createService(Map<String, String> properties) {
		assert (getFactoryID() != null);
		String serviceID = getFactoryID() + "-" + (++counter);
		try {
			int val = Integer.parseInt(properties.get(Constants.SERVICEID)
					.split("-")[1]);
			if (val > counter) {
				counter = val;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			logger
					.error("Service name doesn't follow <factoryid>-<serviceid>: "
							+ properties.get(Constants.SERVICEID));
			return;
		}
		createService(serviceID, properties);
	}
	
	/**
	 * Get the class this factory constructs.
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
