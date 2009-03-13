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
	 * org.rifidi.edge.lr.LogicalReaderManagementService#createLogicalReader
	 * (java.lang.String, org.rifidi.edge.epcglobal.ale.api.lr.data.LRSpec,
	 * java.lang.Boolean)
	 */
	@Override
	public void createLogicalReader(String name, LRSpec lrSpec,
			Boolean immutable) throws DuplicateNameExceptionResponse {
		logger.debug("Creating reader " + name);
		if (!readers.containsKey(name)) {
			Set<LogicalReader> readers = new HashSet<LogicalReader>();
			for (String reader : lrSpec.getReaders().getReader()) {
				readers.add(this.readers.get(reader));
			}
			Map<String, String> props = new HashMap<String, String>();
			for (LRProperty prop : lrSpec.getProperties().getProperty()) {
				props.put(prop.getName(), prop.getValue());
			}
			if (lrSpec.isIsComposite()) {
				readers.add(new CompositeLogicalReaderImpl(immutable, name,
						props, readers));
				return;
			}
			readers.add(new LogicalReaderImpl(immutable, name, props));
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
		LogicalReader reader = getLogicalReaderByName(name);
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
		logger.debug("Binding reader " + reader.getName());
		synchronized (this.readers) {
			this.readers.put(reader.getName(), new LogicalReaderImpl(true,
					reader.getName(), new HashMap<String, String>()));
		}
	}

	/**
	 * Unregister a physical reader.
	 */
	public void unbindReader(AbstractReader<?> reader,
			Dictionary<Object, Object> props) {
		logger.debug("Unbinding reader " + reader.getName());
		synchronized (this.readers) {
			if (this.readers.get(reader.getName()).isInUse()) {
				logger.warn("Removing reader that is currently used: "
						+ reader.getName());
			}
			// TODO: add better removal logic
			this.readers.remove(reader.getName());
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
