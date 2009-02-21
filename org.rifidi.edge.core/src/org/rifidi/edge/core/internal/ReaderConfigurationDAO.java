package org.rifidi.edge.core.internal;

import java.util.Set;

import org.rifidi.edge.core.readers.AbstractReaderConfigurationFactory;
import org.rifidi.edge.core.readers.ReaderConfiguration;

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

	/**
	 * Get a readerConfiguration with the given id
	 * 
	 * @param readerConfigurationID
	 * @return The readerConfiguration with the supplied ID, or null if it does
	 *         not exist
	 */
	public ReaderConfiguration<?> getReaderConfiguration(
			String readerConfigurationID);

	/**
	 * Get the set of currently defined reader configurations
	 * 
	 * @return
	 */
	public Set<ReaderConfiguration<?>> getCurrentReaderConfigurations();

}
