/**
 * 
 */
package org.rifidi.edge.core.daos;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.notifications.NotifierService;
import org.rifidi.edge.core.notifications.NotifierServiceWrapper;
import org.rifidi.edge.core.readers.AbstractReader;
import org.rifidi.edge.core.readers.AbstractReaderFactory;

/**
 * @author Jochen Mader - jochen@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ReaderDAOImpl implements ReaderDAO {

	/** The available readerSession configuration factories */
	private Map<String, AbstractReaderFactory<?>> readerConfigFactories;
	/** The available set of readerSession configurations */
	private Map<String, AbstractReader<?>> abstractReaders;
	/** The logger for this class */
	private Log logger = LogFactory.getLog(ReaderDAOImpl.class);
	/** A notifier for JMS. Remove once we have aspects */
	private NotifierServiceWrapper notifierService;

	/**
	 * Constructor.
	 */
	public ReaderDAOImpl() {
		readerConfigFactories = new HashMap<String, AbstractReaderFactory<?>>();
		abstractReaders = new HashMap<String, AbstractReader<?>>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.internal.ReaderDAO#getReaderByID(java.lang.String)
	 */
	@Override
	public AbstractReader<?> getReaderByID(String id) {
		return this.abstractReaders.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.internal.ReaderDAO#getReaderFactories()
	 */
	@Override
	public Set<AbstractReaderFactory<?>> getReaderFactories() {
		return new HashSet<AbstractReaderFactory<?>>(readerConfigFactories
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
	public AbstractReaderFactory<?> getReaderFactoryByID(String id) {
		return readerConfigFactories.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.internal.ReaderDAO#getReaders()
	 */
	@Override
	public Set<AbstractReader<?>> getReaders() {
		return new HashSet<AbstractReader<?>>(abstractReaders.values());
	}

	/**
	 * Used by spring to bind a new Reader Factory to this service.
	 * 
	 * @param readerFactory
	 *            the factory to bind
	 * @param parameters
	 */
	public void bindReaderFactory(AbstractReaderFactory<?> readerFactory,
			Dictionary<String, String> parameters) {
		logger.info("Reader Factory Bound:"
				+ readerFactory.getFactoryIDs().get(0));
		readerConfigFactories.put(readerFactory.getFactoryIDs().get(0),
				readerFactory);

		// TODO: Remove once we have aspects
		if (notifierService == null) {
			return;
		}
		NotifierService service = notifierService.getService();
		if (service != null) {
			service.addReaderFactoryEvent(readerFactory.getFactoryIDs().get(0));
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
	public void unbindReaderFactory(AbstractReaderFactory<?> readerFactory,
			Dictionary<String, String> parameters) {
		logger.info("Reader Factory unbound:"
				+ readerFactory.getFactoryIDs().get(0));
		readerConfigFactories.remove(readerFactory.getFactoryIDs().get(0));

		// TODO: Remove once we have aspects
		if (notifierService == null) {
			return;
		}
		NotifierService service = notifierService.getService();
		if (service != null) {
			service.removeReaderFactoryEvent(readerFactory.getFactoryIDs().get(0));
		}
	}

	/**
	 * Used by spring to give the initial list of reader factories.
	 * 
	 * @param factories
	 *            the initial list of available reader factories
	 */
	public void setReaderFactories(Set<AbstractReaderFactory<?>> factories) {
		for (AbstractReaderFactory<?> factory : factories) {
			readerConfigFactories.put(factory.getFactoryIDs().get(0), factory);
		}

		// TODO: Remove once we have aspects
		if (notifierService == null) {
			return;
		}
		NotifierService service = notifierService.getService();
		if (service != null) {
			for(AbstractReaderFactory<?> factory : factories){
				service.addReaderFactoryEvent(factory.getFactoryIDs().get(0));
			}
		}
	}

	/**
	 * Used by spring to bind a new AbstractReader to this service.
	 * 
	 * @param reader
	 *            the configuration to bind
	 * @param parameters
	 */
	public void bindReader(AbstractReader<?> reader,
			Dictionary<String, String> parameters) {
		logger.info("Reader bound:" + reader.getID());
		this.abstractReaders.put(reader.getID(), reader);
	}

	/**
	 * Used by spring to unbind a disappearing AbstractReader service from this
	 * service.
	 * 
	 * @param reader
	 *            the AbstractReader to unbind
	 * @param parameters
	 */
	public void unbindReader(AbstractReader<?> reader,
			Dictionary<String, String> parameters) {
		logger.info("Reader unbound:" + reader.getID());
		abstractReaders.remove(reader.getID());
	}

	/**
	 * Used by spring to give the initial list of readers
	 * 
	 * @param readers
	 *            the initial list of available readers
	 */
	public void setReader(Set<AbstractReader<?>> readers) {
		for (AbstractReader<?> reader : readers) {
			abstractReaders.put(reader.getID(), reader);
		}
	}
	
	/**
	 * Called by Spring
	 * 
	 * @param notifierService
	 *            the notifierService to set
	 */
	public void setNotifierService(NotifierServiceWrapper notifierService) {
		this.notifierService = notifierService;
	}
}
