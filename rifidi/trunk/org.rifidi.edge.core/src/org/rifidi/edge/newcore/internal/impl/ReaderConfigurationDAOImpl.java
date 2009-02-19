/**
 * 
 */
package org.rifidi.edge.newcore.internal.impl;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.rifidi.edge.newcore.internal.ReaderConfigurationDAO;
import org.rifidi.edge.newcore.readers.AbstractReaderConfigurationFactory;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ReaderConfigurationDAOImpl implements ReaderConfigurationDAO {

	/** The available reader configuration factories */
	private Set<AbstractReaderConfigurationFactory<?>> readerConfigFactories;

	/**
	 * constructor
	 */
	public ReaderConfigurationDAOImpl() {
		readerConfigFactories = new HashSet<AbstractReaderConfigurationFactory<?>>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.newcore.internal.ReaderConfigurationDAO#
	 * getReaderConfigurationFactory(java.lang.String)
	 */
	@Override
	public AbstractReaderConfigurationFactory<?> getReaderConfigurationFactory(
			String readerConfigurationFactoryID) {
		Iterator<AbstractReaderConfigurationFactory<?>> iter = readerConfigFactories
				.iterator();
		AbstractReaderConfigurationFactory<?> current = null;
		while (iter.hasNext()) {
			current = iter.next();
			if (current.getFactoryIDs().get(0).equals(
					readerConfigurationFactoryID)) {
				return current;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.newcore.internal.ReaderConfigurationDAO#
	 * getCurrentReaderConfigurationFactories()
	 */
	@Override
	public Set<AbstractReaderConfigurationFactory<?>> getCurrentReaderConfigurationFactories() {
		return new HashSet<AbstractReaderConfigurationFactory<?>>(
				readerConfigFactories);
	}

	/**
	 * Used by spring to bind a new ReaderConfigurationFactory to this service.
	 * 
	 * @param readerConfigurationFactory
	 *            the factory to bind
	 * @param parameters
	 */
	public void bindReaderConfigFactory(
			AbstractReaderConfigurationFactory<?> readerConfigurationFactory,
			Dictionary<String, String> parameters) {
		readerConfigFactories.add(readerConfigurationFactory);
	}

	/**
	 * Used by spring to unbind a disappearing ReaderConfigurationFactory
	 * service from this service.
	 * 
	 * @param readerConfigurationFactory
	 *            the ReaderConfigurationFactory to unbind
	 * @param parameters
	 */
	public void unbindReaderConfigFactory(
			AbstractReaderConfigurationFactory<?> readerConfigurationFactory,
			Dictionary<String, String> parameters) {
		readerConfigFactories.remove(readerConfigurationFactory);
	}

	/**
	 * Used by spring to give the initial list of reader configuration
	 * factories.
	 * 
	 * @param factories
	 *            the initial list of available reader configuration factories
	 */
	public void setReaderConfigFactories(
			Set<AbstractReaderConfigurationFactory<?>> factories) {
		readerConfigFactories.addAll(factories);
	}

}
