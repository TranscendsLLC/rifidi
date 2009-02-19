/**
 * 
 */
package org.rifidi.edge.newcore.readers;

import org.rifidi.configuration.AbstractServiceFactory;

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
	 * AbstractCommandConfigurationFactories need to be tied to a specific
	 * reader plugin type. This method should return the ID of the
	 * CommandConfiguration associated with this reader plugin
	 * 
	 * @return
	 */
	public abstract String getCommandConfigurationFactoryName();

}
