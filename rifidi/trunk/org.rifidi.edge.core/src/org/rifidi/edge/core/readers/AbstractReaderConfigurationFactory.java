/**
 * 
 */
package org.rifidi.edge.core.readers;

import org.rifidi.configuration.impl.AbstractServiceFactory;

/**
 * An abstract class for all ReaderConfigurationFactories to extend.
 * ReaderConfigurationFactories should register themselves to osgi under both
 * the AbstractReaderConfigurationFactory and the
 * org.rifidi.configuration.ServiceFactory interfaces
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class AbstractReaderConfigurationFactory<T extends ReaderConfiguration<?>>
		extends AbstractServiceFactory<T> {

	/**
	 * Get the ID of the CommandConfigurationFactory associated with this reader
	 * configuration
	 * 
	 * @return
	 */
	public abstract String getCommandConfigFactoryID();

}
