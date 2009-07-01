package org.rifidi.edge.core.daos.internal.impl;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.daos.ReaderDAO;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.base.AbstractSensorFactory;
import org.rifidi.edge.core.services.notification.NotifierService;

/**
 * Implementation of Reader Data Access Object. Allows components to access
 * readers
 * 
 * @author Jochen Mader - jochen@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class ReaderDAOImpl implements ReaderDAO {

	/** The available sensorSession configuration factories */
	private Map<String, AbstractSensorFactory<?>> readerConfigFactories;
	/** The available set of sensorSession configurations */
	private Map<String, AbstractSensor<?>> abstractSensors;
	/** The logger for this class */
	private Log logger = LogFactory.getLog(ReaderDAOImpl.class);
	/** A notifier for JMS. Remove once we have aspects */
	private NotifierService notifierService;

	/**
	 * Constructor.
	 */
	public ReaderDAOImpl() {
		readerConfigFactories = new HashMap<String, AbstractSensorFactory<?>>();
		abstractSensors = new HashMap<String, AbstractSensor<?>>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.internal.ReaderDAO#getReaderByID(java.lang.String)
	 */
	@Override
	public AbstractSensor<?> getReaderByID(String id) {
		return this.abstractSensors.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.internal.ReaderDAO#getReaderFactories()
	 */
	@Override
	public Set<AbstractSensorFactory<?>> getReaderFactories() {
		return new HashSet<AbstractSensorFactory<?>>(readerConfigFactories
				.values());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.internal.ReaderDAO#getReaderFactoryByID(java.lang
	 * .String)
	 */
	@Override
	public AbstractSensorFactory<?> getReaderFactoryByID(String id) {
		return readerConfigFactories.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.internal.ReaderDAO#getReaders()
	 */
	@Override
	public Set<AbstractSensor<?>> getReaders() {
		return new HashSet<AbstractSensor<?>>(abstractSensors.values());
	}

	/**
	 * Used by spring to bind a new Reader Factory to this service.
	 * 
	 * @param readerFactory
	 *            the factory to bind
	 * @param parameters
	 */
	public void bindReaderFactory(AbstractSensorFactory<?> readerFactory,
			Dictionary<String, String> parameters) {
		logger.info("Reader Factory Bound:"
				+ readerFactory.getFactoryIDs().get(0));
		readerConfigFactories.put(readerFactory.getFactoryIDs().get(0),
				readerFactory);

		// TODO: Remove once we have aspects
		if (notifierService != null) {
			notifierService.addReaderFactoryEvent(readerFactory.getFactoryIDs()
					.get(0));
		}
	}

	/**
	 * Used by spring to unbind a disappearing ReaderConfigurationFactory
	 * service from this service.
	 * 
	 * @param readerFactory
	 *            the ReaderFactory to unbind
	 * @param parameters
	 */
	public void unbindReaderFactory(AbstractSensorFactory<?> readerFactory,
			Dictionary<String, String> parameters) {
		logger.info("Reader Factory unbound:"
				+ readerFactory.getFactoryIDs().get(0));
		readerConfigFactories.remove(readerFactory.getFactoryIDs().get(0));

		// TODO: Remove once we have aspects
		if (notifierService != null) {
			notifierService.removeReaderFactoryEvent(readerFactory
					.getFactoryIDs().get(0));
		}
	}

	/**
	 * Used by spring to give the initial list of reader factories.
	 * 
	 * @param factories
	 *            the initial list of available reader factories
	 */
	public void setReaderFactories(Set<AbstractSensorFactory<?>> factories) {
		for (AbstractSensorFactory<?> factory : factories) {
			readerConfigFactories.put(factory.getFactoryIDs().get(0), factory);
		}

		// TODO: Remove once we have aspects
		if (notifierService != null) {
			for (AbstractSensorFactory<?> factory : factories) {
				notifierService.addReaderFactoryEvent(factory.getFactoryIDs()
						.get(0));
			}
		}

	}

	/**
	 * Used by spring to bind a new AbstractSensor to this service.
	 * 
	 * @param reader
	 *            the configuration to bind
	 * @param parameters
	 */
	public void bindReader(AbstractSensor<?> reader,
			Dictionary<String, String> parameters) {
		logger.info("Reader bound:" + reader.getID());
		this.abstractSensors.put(reader.getID(), reader);
	}

	/**
	 * Used by spring to unbind a disappearing AbstractSensor service from this
	 * service.
	 * 
	 * @param reader
	 *            the AbstractSensor to unbind
	 * @param parameters
	 */
	public void unbindReader(AbstractSensor<?> reader,
			Dictionary<String, String> parameters) {
		logger.info("Reader unbound:" + reader.getID());
		abstractSensors.remove(reader.getID());
	}

	/**
	 * Used by spring to give the initial list of readers
	 * 
	 * @param readers
	 *            the initial list of available readers
	 */
	public void setReader(Set<AbstractSensor<?>> readers) {
		for (AbstractSensor<?> reader : readers) {
			abstractSensors.put(reader.getID(), reader);
		}
	}

	/**
	 * Called by Spring
	 * 
	 * @param notifierService
	 *            the notifierService to set
	 */
	public void setNotifierService(NotifierService notifierService) {
		this.notifierService = notifierService;
	}
}
