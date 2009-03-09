/**
 * 
 */
package org.rifidi.edge.core.readers;

import org.rifidi.configuration.impl.AbstractServiceFactory;

/**
 * An abstract class for all ReaderConfigurationFactories to extend.
 * ReaderConfigurationFactories should register themselves to osgi under both
 * the AbstractReaderFactory and the
 * org.rifidi.configuration.ServiceFactory interfaces
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class AbstractReaderFactory<T extends AbstractReader<?>>
		extends AbstractServiceFactory<T> {
}
