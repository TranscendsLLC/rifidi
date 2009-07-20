/**
 * 
 */
package org.rifidi.configuration.impl;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
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
public abstract class AbstractMultiServiceFactory<T> implements ServiceFactory<T> {
	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(AbstractMultiServiceFactory.class);
	/** Context of the registering bundle. */
	private BundleContext context;

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
