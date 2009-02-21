/**
 * 
 */
package org.rifidi.edge.core.internal.impl;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.rifidi.edge.core.internal.ReaderConfigurationDAO;
import org.rifidi.edge.core.readers.AbstractReaderConfigurationFactory;
import org.rifidi.edge.core.readers.ReaderConfiguration;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ReaderConfigurationDAOImpl implements ReaderConfigurationDAO {

	/** The available reader configuration factories */
	private Set<AbstractReaderConfigurationFactory<?>> readerConfigFactories;
	/** The available set of reader configurations */
	private Set<ReaderConfiguration<?>> readerConfigurations;

	/**
	 * constructor
	 */
	public ReaderConfigurationDAOImpl() {
		readerConfigFactories = new HashSet<AbstractReaderConfigurationFactory<?>>();
		readerConfigurations = new HashSet<ReaderConfiguration<?>>();
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

	@Override
	public Set<ReaderConfiguration<?>> getCurrentReaderConfigurations() {
		return new HashSet<ReaderConfiguration<?>>(readerConfigurations);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.internal.ReaderConfigurationDAO#getReaderConfiguration
	 * (java.lang.String)
	 */
	@Override
	public ReaderConfiguration<?> getReaderConfiguration(
			String readerConfigurationID) {
		Iterator<ReaderConfiguration<?>> iter = readerConfigurations.iterator();
		while (iter.hasNext()) {
			ReaderConfiguration<?> current = iter.next();
			if (current.getID().equals(readerConfigurationID)) {
				return current;
			}
		}
		return null;
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

	/**
	 * Used by spring to bind a new ReaderConfiguration to this service.
	 * 
	 * @param readerConfiguration
	 *            the configuration to bind
	 * @param parameters
	 */
	public void bindReaderConfiguration(
			ReaderConfiguration<?> readerConfiguration,
			Dictionary<String, String> parameters) {
		this.readerConfigurations.add(readerConfiguration);
	}

	/**
	 * Used by spring to unbind a disappearing ReaderConfiguration service from
	 * this service.
	 * 
	 * @param readerConfiguration
	 *            the ReaderConfiguration to unbind
	 * @param parameters
	 */
	public void unbindReaderConfiguration(
			ReaderConfiguration<?> readerConfiguration,
			Dictionary<String, String> parameters) {
		readerConfigurations.remove(readerConfiguration);
	}

	/**
	 * Used by spring to give the initial list of reader configurations
	 * 
	 * @param configurations
	 *            the initial list of available reader configurations
	 */
	public void setReaderConfigurations(
			Set<ReaderConfiguration<?>> configurations) {
		readerConfigurations.addAll(configurations);
	}

}
