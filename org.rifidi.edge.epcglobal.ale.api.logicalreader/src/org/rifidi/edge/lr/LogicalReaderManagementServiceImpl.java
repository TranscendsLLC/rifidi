/**
 * 
 */
package org.rifidi.edge.lr;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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

	private Map<String, LogicalReader> readers;

	/**
	 * Constructor.
	 */
	public LogicalReaderManagementServiceImpl() {
		readers = new ConcurrentHashMap<String, LogicalReader>();
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
		LogicalReader reader = getLogicalReaderByName(name);
		reader.destroy();
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

}
