/**
 * 
 */
package org.rifidi.edge.lr;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.ReentrantLock;

import org.rifidi.edge.epcglobal.ale.api.lr.data.LRProperty;
import org.rifidi.edge.epcglobal.ale.api.lr.data.LRSpec;
import org.rifidi.edge.epcglobal.ale.api.lr.data.LRSpec.Properties;
import org.rifidi.edge.epcglobal.ale.api.lr.ws.ImmutableReaderExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.lr.ws.InUseExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.lr.ws.ReaderLoopExceptionResponse;
import org.rifidi.edge.lr.exceptions.ImmutableReaderException;
import org.rifidi.edge.lr.exceptions.ReaderInUseException;

/**
 * Only physical readers are represented by this type of logical readers.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class LogicalReaderImpl implements LogicalReader {
	/** Objects using the reader. */
	private Set<Object> users;
	/** Name of the reader. */
	private String name;
	/** Properties of the reader. */
	private Map<String, String> properties;
	/** True if the reader can't be changed or deleted. */
	private Boolean immutable;
	/** Set containing all parents of this reader. */
	private Set<LogicalReader> parents;
	/** Locked during every update for a reader. */
	private ReentrantLock updating = new ReentrantLock();
	/** True if the reader was destroyed. */
	private boolean destroyed = false;
	/** true if the reader has parents or is used. */
	private boolean inUse = false;

	/**
	 * Constructor.
	 * 
	 * @param immutable
	 * @param name
	 * @param properties
	 */
	public LogicalReaderImpl(Boolean immutable, String name,
			Set<LogicalReader> readers, Map<String, String> properties) {
		if (immutable == null) {
			throw new IllegalArgumentException("immutable must not be null");
		}
		if (name == null) {
			throw new IllegalArgumentException("name must not be null");
		}
		if (readers != null && readers.size() > 0) {
			throw new IllegalArgumentException(
					"This type of reader can't have sub readers.");
		}
		if (properties == null) {
			throw new IllegalArgumentException("properties must not be null");
		}
		this.immutable = immutable;
		this.name = name;
		this.properties = new ConcurrentHashMap<String, String>(properties);
		this.users = new CopyOnWriteArraySet<Object>();
		this.parents = new HashSet<LogicalReader>();
		for (LogicalReader reader : readers) {
			reader.makeChildOf(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.lr.LogicalReader#aquire(java.lang.Object)
	 */
	@Override
	public void aquire(Object user) {
		updating.lock();
		try {
			users.add(user);
			inUse = true;
		} finally {
			updating.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.lr.LogicalReader#destroy()
	 */
	@Override
	public void destroy() throws ImmutableReaderException, ReaderInUseException {
		updating.lock();
		try {
			if (immutable) {
				throw new ImmutableReaderException("Reader is immutable.");
			}
			if (!users.isEmpty()) {
				throw new ReaderInUseException("There are " + users.size()
						+ " users using the reader.");
			}
			if (hasParents()) {
				throw new ReaderInUseException("There are " + parents.size()
						+ " readers using the reader.");
			}
			destroyed = true;
		} finally {
			updating.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.lr.LogicalReader#getLRSpec()
	 */
	@Override
	public LRSpec getLRSpec() {
		LRSpec lrSpec = new LRSpec();
		lrSpec.setIsComposite(false);
		Properties props = new Properties();
		for (String key : properties.keySet()) {
			LRProperty prop = new LRProperty();
			prop.setName(key);
			prop.setValue(properties.get(key));
			props.getProperty().add(prop);
		}
		lrSpec.setProperties(props);
		// no readers in a non composite reader
		lrSpec.setReaders(new LRSpec.Readers());
		return lrSpec;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.lr.LogicalReader#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.lr.LogicalReader#getProperty(java.lang.String)
	 */
	@Override
	public String getProperty(String propertyName) {
		return properties.get(propertyName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.lr.LogicalReader#getUsers()
	 */
	@Override
	public Set<Object> getUsers() {
		return new HashSet<Object>(users);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.lr.LogicalReader#isImmutable()
	 */
	@Override
	public boolean isImmutable() {
		return immutable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.lr.LogicalReader#isInUse()
	 */
	@Override
	public boolean isInUse() {
		return inUse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.lr.LogicalReader#release(java.lang.Object)
	 */
	@Override
	public void release(Object user) {
		updating.lock();
		try {
			users.remove(user);
			if (parents.isEmpty() && users.isEmpty()) {
				inUse = false;
			}
		} finally {
			updating.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.lr.LogicalReader#setProperty(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void setProperty(String propertyName, String propertyValue)
			throws ImmutableReaderExceptionResponse, InUseExceptionResponse {
		updating.lock();
		try {
			if (!inUse) {
				if (!immutable) {
					this.properties.put(propertyName, propertyValue);
					return;
				} else {
					throw new ImmutableReaderExceptionResponse(
							"Reader is immutable.");
				}
			}
			throw new InUseExceptionResponse("Reader is in use.");
		} finally {
			updating.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.lr.LogicalReader#update(java.util.Map,
	 * java.util.Set)
	 */
	@Override
	public void update(Map<String, String> properties,
			Set<LogicalReader> readers)
			throws ImmutableReaderExceptionResponse, InUseExceptionResponse,
			ReaderLoopExceptionResponse {
		updating.lock();
		try {
			if (readers != null && readers.size() > 0) {
				throw new IllegalArgumentException(
						"This type of reader can't have sub readers.");
			}
			if (!inUse) {
				if (!isImmutable()) {
					this.properties.clear();
					for (String key : properties.keySet()) {
						this.properties.put(key, properties.get(key));
					}
					return;
				} else {
					throw new ImmutableReaderExceptionResponse(
							"Reader is immutable.");
				}
			}
			throw new InUseExceptionResponse("Reader is in use.");
		} finally {
			updating.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.lr.LogicalReader#getReaders()
	 */
	@Override
	public Set<LogicalReader> getReaders() {
		return Collections.emptySet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.lr.LogicalReader#growUp(org.rifidi.edge.lr.LogicalReader)
	 */
	@Override
	public void growUp(LogicalReader parent) {
		updating.lock();
		try {
			parents.remove(parent);
			if (parents.isEmpty() && users.isEmpty()) {
				inUse = false;
			}
		} finally {
			updating.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.lr.LogicalReader#makeChildOf(org.rifidi.edge.lr.LogicalReader
	 * )
	 */
	@Override
	public void makeChildOf(LogicalReader parent) {
		updating.lock();
		try {
			parents.add(parent);
			inUse = true;
		} finally {
			updating.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.lr.LogicalReader#hasparents()
	 */
	@Override
	public boolean hasParents() {
		return !parents.isEmpty();
	}

}
