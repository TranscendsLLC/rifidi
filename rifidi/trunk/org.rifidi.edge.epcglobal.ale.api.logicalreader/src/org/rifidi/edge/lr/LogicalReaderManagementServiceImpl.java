/**
 * 
 */
package org.rifidi.edge.lr;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.readers.AbstractReader;
import org.rifidi.edge.epcglobal.ale.api.lr.data.LRProperty;
import org.rifidi.edge.epcglobal.ale.api.lr.data.LRSpec;
import org.rifidi.edge.epcglobal.ale.api.lr.ws.DuplicateNameExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.lr.ws.ImmutableReaderExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.lr.ws.InUseExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.lr.ws.ValidationExceptionResponse;
import org.rifidi.edge.esper.EsperManagementService;
import org.rifidi.edge.lr.exceptions.DuplicateReaderNameException;
import org.rifidi.edge.lr.exceptions.ImmutableReaderException;
import org.rifidi.edge.lr.exceptions.NoSuchReaderNameException;
import org.rifidi.edge.lr.exceptions.ReaderInUseException;

import com.espertech.esper.client.EPStatement;

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
	private Map<String, ReaderStatementTuple> readers;

	/** Reference to the service managing the esper provider. */
	private EsperManagementService esperManagementService;

	/**
	 * Constructor.
	 */
	public LogicalReaderManagementServiceImpl() {
		readers = new HashMap<String, ReaderStatementTuple>();
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
			Boolean immutable) throws DuplicateReaderNameException,
			NoSuchReaderNameException {
		logger.debug("Creating reader " + name);
		synchronized (readers) {
			if (!readers.containsKey(name)) {
				Set<LogicalReader> readers = new HashSet<LogicalReader>();
				// check if we got readers
				if (lrSpec.getReaders() == null) {
					throw new NoSuchReaderNameException(
							"No reader names were selected for this spec.");
				}
				// check if all readers really exist
				if (!this.readers.keySet().containsAll(
						lrSpec.getReaders().getReader())) {
					throw new NoSuchReaderNameException(
							"An invalid reader was specified. ");
				}
				// create the list of readers for the new composite reader
				for (String reader : lrSpec.getReaders().getReader()) {
					readers.add(this.readers.get(reader).reader);
				}

				Map<String, String> props = new HashMap<String, String>();
				if (lrSpec.getProperties() != null) {
					for (LRProperty prop : lrSpec.getProperties().getProperty()) {
						props.put(prop.getName(), prop.getValue());
					}
				}
				ReaderStatementTuple tuple = new ReaderStatementTuple();
				if (lrSpec.isIsComposite()) {
					tuple.reader = new CompositeLogicalReaderImpl(immutable,
							name, props, readers);
					logger.debug("Created new composite reader: " + name);
				} else {
					tuple.reader = new LogicalReaderImpl(immutable, name,
							readers, props);
					logger.debug("Created new logical reader: " + name);
				}
				tuple.statement = createReaderInEsper(tuple.reader);
				this.readers.put(name, tuple);
				return;
			}
			throw new DuplicateReaderNameException("A reader named " + name
					+ " already exists.");
		}
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
			throws NoSuchReaderNameException, ImmutableReaderException,
			ReaderInUseException {
		logger.debug("Trying to destroy reader " + name);
		synchronized (readers) {
			LogicalReader reader = readers.get(name).reader;
			if (reader == null) {
				logger.warn("Trying to delete nonexisten reader " + name);
				throw new NoSuchReaderNameException("Reader with name " + name
						+ " does not exist.");
			}
			if (reader.isImmutable()) {
				throw new ImmutableReaderException("Reader " + name
						+ " is immutable. ");
			}
			if (reader.isInUse()) {
				throw new ReaderInUseException("Reader " + name
						+ " is currently in use by an ECSpec.");
			}
			if (reader.hasParents()) {
				throw new ReaderInUseException("Reader " + name
						+ " is currently in use by other readers.");
			}
			readers.remove(name);
			reader.destroy();
		}
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
			throws NoSuchReaderNameException {
		LogicalReader reader = readers.get(name).reader;
		if (reader == null) {
			throw new NoSuchReaderNameException(" No reader named " + name
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
			ReaderStatementTuple tuple = new ReaderStatementTuple();
			tuple.reader = new LogicalReaderImpl(true, reader.getID(),
					new HashSet<LogicalReader>(), new HashMap<String, String>());
			tuple.statement = createReaderInEsper(tuple.reader);
			this.readers.put(reader.getID(), tuple);
			logger.debug("Created new logical reader: " + reader.getID());
		}
	}

	/**
	 * Unregister a physical reader.
	 */
	public void unbindReader(AbstractReader<?> reader,
			Dictionary<Object, Object> props) {
		logger.debug("Unbinding reader " + reader.getID());
		synchronized (this.readers) {
			if (this.readers.get(reader.getID()).reader.isInUse()) {
				logger.warn("Removing reader that is currently used: "
						+ reader.getID());
			}
			ReaderStatementTuple tuple = this.readers.remove(reader.getID());
			tuple.statement.destroy();
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

	/**
	 * Set the esper service.
	 * 
	 * @param esperManagementService
	 */
	public void setEsperManagementService(
			EsperManagementService esperManagementService) {
		synchronized (readers) {
			// replacing old service with new one
			if (this.esperManagementService != null) {
				for (ReaderStatementTuple tuple : readers.values()) {
					tuple.statement = createReaderInEsper(tuple.reader);
				}
				this.esperManagementService = esperManagementService;
				return;
			}
			this.esperManagementService = esperManagementService;
		}
	}

	/**
	 * Create a new stream in esper with the reader name as its name. Not
	 * threadsafe. Returns null if no esper service is available.
	 * 
	 * @param reader
	 * @return
	 */
	private EPStatement createReaderInEsper(LogicalReader reader) {
		if (esperManagementService == null) {
			return null;
		}
		StringBuilder builder = new StringBuilder();
		getReaderNames(reader, builder);
		builder.deleteCharAt(builder.length() - 1);
		return esperManagementService
				.getProvider()
				.getEPAdministrator()
				.createEPL(
						"insert into "
								+ reader.getName()
								+ " select * from TagReadEvent where readerID in ("
								+ builder.toString() + ")");
	}

	/**
	 * Helper method for filling the given set with the names of physicals
	 * readers.
	 * 
	 * @param readerNames
	 */
	private void getReaderNames(LogicalReader reader, StringBuilder readerNames) {
		if (reader instanceof LogicalReaderImpl) {
			readerNames.append("'");
			readerNames.append(reader.getName());
			readerNames.append("'");
			readerNames.append(",");
			return;
		}
		for (LogicalReader subReader : reader.getReaders()) {
			getReaderNames(subReader, readerNames);
		}
	}

	private class ReaderStatementTuple {
		public LogicalReader reader;
		public EPStatement statement;
	}
}
