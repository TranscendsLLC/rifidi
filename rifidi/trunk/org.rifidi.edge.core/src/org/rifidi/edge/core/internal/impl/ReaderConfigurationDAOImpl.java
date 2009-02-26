/**
 * 
 */
package org.rifidi.edge.core.internal.impl;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.internal.ReaderConfigurationDAO;
import org.rifidi.edge.core.readers.AbstractReaderConfigurationFactory;
import org.rifidi.edge.core.readers.AbstractReaderConfiguration;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ReaderConfigurationDAOImpl implements ReaderConfigurationDAO {

	/** The available reader configuration factories */
	private Set<AbstractReaderConfigurationFactory<?>> readerConfigFactories;
	/** The available set of reader configurations */
	private Set<AbstractReaderConfiguration<?>> abstractReaderConfigurations;
	/** The logger for this class */
	private Log logger = LogFactory.getLog(ReaderConfigurationDAOImpl.class);

	/**
	 * constructor
	 */
	public ReaderConfigurationDAOImpl() {
		readerConfigFactories = new HashSet<AbstractReaderConfigurationFactory<?>>();
		abstractReaderConfigurations = new HashSet<AbstractReaderConfiguration<?>>();
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
	public Set<AbstractReaderConfiguration<?>> getCurrentReaderConfigurations() {
		return new HashSet<AbstractReaderConfiguration<?>>(abstractReaderConfigurations);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.internal.ReaderConfigurationDAO#getReaderConfiguration
	 * (java.lang.String)
	 */
	@Override
	public AbstractReaderConfiguration<?> getReaderConfiguration(
			String readerConfigurationID) {
		Iterator<AbstractReaderConfiguration<?>> iter = abstractReaderConfigurations.iterator();
		while (iter.hasNext()) {
			AbstractReaderConfiguration<?> current = iter.next();
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
		logger.info("Reader Cofiguration Factory Bound:"
				+ readerConfigurationFactory.getFactoryIDs().get(0));
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
		logger.info("Reader Cofiguration Factory unbound:"
				+ readerConfigurationFactory.getFactoryIDs().get(0));
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
		Iterator<AbstractReaderConfigurationFactory<?>> iter = factories
				.iterator();
		readerConfigFactories.addAll(factories);
	}

	/**
	 * Used by spring to bind a new AbstractReaderConfiguration to this service.
	 * 
	 * @param readerConfiguration
	 *            the configuration to bind
	 * @param parameters
	 */
	public void bindReaderConfiguration(
			AbstractReaderConfiguration<?> readerConfiguration,
			Dictionary<String, String> parameters) {
		logger.info("New Reader Cofiguration Bound:" + readerConfiguration.getID());
		this.abstractReaderConfigurations.add(readerConfiguration);
	}

	/**
	 * Used by spring to unbind a disappearing AbstractReaderConfiguration service from
	 * this service.
	 * 
	 * @param readerConfiguration
	 *            the AbstractReaderConfiguration to unbind
	 * @param parameters
	 */
	public void unbindReaderConfiguration(
			AbstractReaderConfiguration<?> readerConfiguration,
			Dictionary<String, String> parameters) {
		logger.info("Reader Configuraiton unbound:" + readerConfiguration.getID());
		abstractReaderConfigurations.remove(readerConfiguration);
	}

	/**
	 * Used by spring to give the initial list of reader configurations
	 * 
	 * @param configurations
	 *            the initial list of available reader configurations
	 */
	public void setReaderConfiguration(
			Set<AbstractReaderConfiguration<?>> configurations) {
		Iterator<AbstractReaderConfiguration<?>> configs = configurations.iterator();
		abstractReaderConfigurations.addAll(configurations);
	}

}
