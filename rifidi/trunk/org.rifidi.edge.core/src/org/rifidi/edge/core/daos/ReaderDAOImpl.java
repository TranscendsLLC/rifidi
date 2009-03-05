/**
 * 
 */
package org.rifidi.edge.core.daos;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.readers.AbstractReader;
import org.rifidi.edge.core.readers.AbstractReaderFactory;

/**
 * @author Jochen Mader - jochen@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ReaderDAOImpl implements ReaderDAO {

	/** The available readerSession configuration factories */
	private Set<AbstractReaderFactory<?>> readerConfigFactories;
	/** The available set of readerSession configurations */
	private Set<AbstractReader<?>> abstractReaders;
	/** The logger for this class */
	private Log logger = LogFactory.getLog(ReaderDAOImpl.class);

	/**
	 * Constructor.
	 */
	public ReaderDAOImpl() {
		readerConfigFactories = new HashSet<AbstractReaderFactory<?>>();
		abstractReaders = new HashSet<AbstractReader<?>>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.internal.ReaderDAO#getReaderByID(java.lang.String)
	 */
	@Override
	public AbstractReader<?> getReaderByID(String id) {
		Iterator<AbstractReader<?>> iter = abstractReaders.iterator();
		while (iter.hasNext()) {
			AbstractReader<?> current = iter.next();
			if (current.getID().equals(id)) {
				return current;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.internal.ReaderDAO#getReaderFactories()
	 */
	@Override
	public Set<AbstractReaderFactory<?>> getReaderFactories() {
		return new HashSet<AbstractReaderFactory<?>>(readerConfigFactories);
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
		Iterator<AbstractReaderFactory<?>> iter = readerConfigFactories
				.iterator();
		AbstractReaderFactory<?> current = null;
		while (iter.hasNext()) {
			current = iter.next();
			if (current.getFactoryIDs().get(0).equals(id)) {
				return current;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.internal.ReaderDAO#getReaders()
	 */
	@Override
	public Set<AbstractReader<?>> getReaders() {
		return new HashSet<AbstractReader<?>>(abstractReaders);
	}

	/**
	 * Used by spring to bind a new Reader Factory to this service.
	 * 
	 * @param readerFactory
	 *            the factory to bind
	 * @param parameters
	 */
	public void bindReaderFactory(
			AbstractReaderFactory<?> readerFactory,
			Dictionary<String, String> parameters) {
		logger.info("Reader Factory Bound:"
				+ readerFactory.getFactoryIDs().get(0));
		readerConfigFactories.add(readerFactory);
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
		readerConfigFactories.remove(readerFactory);
	}

	/**
	 * Used by spring to give the initial list of reader factories.
	 * 
	 * @param factories
	 *            the initial list of available reader factories
	 */
	public void setReaderFactories(Set<AbstractReaderFactory<?>> factories) {
		readerConfigFactories.addAll(factories);
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
		this.abstractReaders.add(reader);
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
		abstractReaders.remove(reader);
	}

	/**
	 * Used by spring to give the initial list of readers
	 * 
	 * @param readers
	 *            the initial list of available readers
	 */
	public void setReader(Set<AbstractReader<?>> readers) {
		abstractReaders.addAll(readers);
	}
}
