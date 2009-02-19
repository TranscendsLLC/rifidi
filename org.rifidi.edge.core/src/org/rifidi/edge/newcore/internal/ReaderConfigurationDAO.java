package org.rifidi.edge.newcore.internal;

import java.util.Set;

import org.rifidi.edge.newcore.readers.AbstractReaderConfigurationFactory;

/**
 * A Data Access Object that keeps track of ReaderConfiguration services and
 * ReaderConfigurationFactory services in the OSGi service registry
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface ReaderConfigurationDAO {

	/**
	 * 
	 * @return A ReaderConfigurationFactory with the supplied ID, or null if it
	 *         does not exist
	 */
	public AbstractReaderConfigurationFactory<?> getReaderConfigurationFactory(
			String readerConfigurationFactoryID);

	/**
	 * 
	 * @return All current ReaderConfigurationFactory services
	 */
	public Set<AbstractReaderConfigurationFactory<?>> getCurrentReaderConfigurationFactories();

}
