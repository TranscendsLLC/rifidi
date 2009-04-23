/**
 * 
 */
package org.rifidi.edge.lr;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.readers.AbstractReader;
import org.rifidi.edge.epcglobal.ale.api.lr.data.LRProperty;
import org.rifidi.edge.epcglobal.ale.api.lr.data.LRSpec;
import org.rifidi.edge.epcglobal.ale.api.lr.ws.DuplicateNameExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.lr.ws.ImmutableReaderExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.lr.ws.InUseExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.lr.ws.NoSuchNameExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.lr.ws.ValidationExceptionResponse;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class LogicalReaderManagementServiceImpl implements
		LogicalReaderManagementService {
	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(LogicalReaderManagementServiceImpl.class);
	/** Map containing all logical readers with their names as key. */
	private Map<String, LogicalReader> readers;

	/**
	 * Constructor.
	 */
	public LogicalReaderManagementServiceImpl() {
		readers = new ConcurrentHashMap<String, LogicalReader>();
		logger.debug("Service created.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.lr.LogicalReaderManagementService#readerExists(java.lang
	 * .String)
	 */
	@Override
	public boolean readerExists(String name) {
		return readers.containsKey(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.lr.LogicalReaderManagementService#createLogicalReader
	 * (java.lang.String, org.rifidi.edge.epcglobal.ale.api.lr.data.LRSpec,
	 * java.lang.Boolean)
	 */
	@Override
	public void createLogicalReader(String name, LRSpec lrSpec,
			Boolean immutable) throws DuplicateNameExceptionResponse,
			ValidationExceptionResponse {
		logger.debug("Creating reader " + name);
		if (!readers.containsKey(name)) {
			Set<LogicalReader> readers = new HashSet<LogicalReader>();
			// check if we got readers
			if (lrSpec.getReaders() == null) {
				throw new ValidationExceptionResponse(
						"No reader names were selected for this spec.");
			}
			// check if all readers really exist
			if (!this.readers.keySet().containsAll(
					lrSpec.getReaders().getReader())) {
				throw new ValidationExceptionResponse(
						"An invalid reader was specified. ");
			}
			// create the list of readers for the new composite reader
			for (String reader : lrSpec.getReaders().getReader()) {
				readers.add(this.readers.get(reader));
			}
			Map<String, String> props = new HashMap<String, String>();
			if (lrSpec.getProperties() != null) {
				for (LRProperty prop : lrSpec.getProperties().getProperty()) {
					props.put(prop.getName(), prop.getValue());
				}
			}
			if (lrSpec.isIsComposite()) {
				this.readers.put(name, new CompositeLogicalReaderImpl(
						immutable, name, props, readers));
				logger.debug("Created new composite reader: "+name);
			} else {
				this.readers.put(name, new LogicalReaderImpl(immutable, name,
						readers, props));
				logger.debug("Created new logical reader: "+name);
			}

			return;
		}
		throw new DuplicateNameExceptionResponse("A reader named " + name
				+ " already exists.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.lr.LogicalReaderManagementService#destroyLogicalReader
	 * (java.lang.String)
	 */
	@Override
	public void destroyLogicalReader(String name)
			throws NoSuchNameExceptionResponse,
			ImmutableReaderExceptionResponse, InUseExceptionResponse {
		logger.debug("Trying to destroy reader " + name);
		LogicalReader reader = readers.get(name);
		if (reader == null) {
			throw new NoSuchNameExceptionResponse("Reader with name " + name
					+ " does not exist.");
		}
		if (reader.isImmutable()) {
			throw new ImmutableReaderExceptionResponse("Reader " + name
					+ " is immutable. ");
		}
		if (reader.isInUse()) {
			throw new InUseExceptionResponse("Reader " + name
					+ " is currently in use by an ECSpec.");
		}
		if (reader.hasParents()) {
			throw new InUseExceptionResponse("Reader " + name
					+ " is currently in use by other readers.");
		}
		readers.remove(name);
		reader.destroy();
		logger.debug("Destroied " + name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.lr.LogicalReaderManagementService#getLogicalReaderByName
	 * (java.lang.String)
	 */
	@Override
	public LogicalReader getLogicalReaderByName(String name)
			throws NoSuchNameExceptionResponse {
		LogicalReader reader = readers.get(name);
		if (reader == null) {
			throw new NoSuchNameExceptionResponse(" No reader named " + name
					+ " available.");
		}
		return reader;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.lr.LogicalReaderManagementService#getLogicalReaders()
	 */
	@Override
	public Set<String> getLogicalReaders() {
		return new HashSet<String>(readers.keySet());
	}

	/**
	 * Register a new physical reader.
	 */
	public void bindReader(AbstractReader<?> reader,
			Dictionary<Object, Object> props) {
		logger.debug("Binding reader " + reader.getID());
		synchronized (this.readers) {
			this.readers.put(reader.getID(), new LogicalReaderImpl(true, reader
					.getID(), new HashSet<LogicalReader>(),
					new HashMap<String, String>()));
		}
	}

	/**
	 * Unregister a physical reader.
	 */
	public void unbindReader(AbstractReader<?> reader,
			Dictionary<Object, Object> props) {
		logger.debug("Unbinding reader " + reader.getID());
		synchronized (this.readers) {
			if (this.readers.get(reader.getID()).isInUse()) {
				logger.warn("Removing reader that is currently used: "
						+ reader.getID());
			}
			// TODO: add better removal logic
			this.readers.remove(reader.getID());
		}
	}

	/**
	 * Used by string to provide the initial list of physical readers.
	 * 
	 * @param readers
	 */
	public void setRealReaders(Set<AbstractReader<?>> readers) {
		synchronized (this.readers) {
			for (AbstractReader<?> reader : readers) {
				bindReader(reader, null);
			}
		}
	}
}
