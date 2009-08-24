/**
 * 
 */
package org.rifidi.edge.core.configuration.impl;

import org.osgi.framework.BundleContext;
import org.rifidi.edge.core.configuration.RifidiService;
import org.rifidi.edge.core.configuration.ServiceFactory;

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
public abstract class AbstractServiceFactory<T extends RifidiService>
		implements ServiceFactory<T> {
	/** Context of the registering bundle. */
	private volatile BundleContext context;

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
