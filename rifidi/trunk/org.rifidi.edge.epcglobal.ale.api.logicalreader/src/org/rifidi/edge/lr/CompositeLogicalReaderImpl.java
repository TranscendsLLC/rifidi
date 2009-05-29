/**
 * 
 */
package org.rifidi.edge.lr;

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
 * This implementation allows to create a tree of logical readers and expose
 * them as a single reader.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class CompositeLogicalReaderImpl implements CompositeLogicalReader {
	/** Objects using the reader. */
	private Set<Object> users;
	/** Name of the reader. */
	private String name;
	/** Properties of the reader. */
	private Map<String, String> properties;
	/** True if the reader can't be changed or deleted. */
	private Boolean immutable;
	/** Set containing all readers that are part of this reader. */
	private Set<LogicalReader> readers;
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
	public CompositeLogicalReaderImpl(Boolean immutable, String name,
			Map<String, String> properties, Set<LogicalReader> readers) {
		if (immutable == null) {
			throw new IllegalArgumentException("immutable must not be null");
		}
		if (name == null) {
			throw new IllegalArgumentException("name must not be null");
		}
		if (readers == null) {
			throw new IllegalArgumentException("readers must not be null");
		}
		if (properties == null) {
			throw new IllegalArgumentException("properties must not be null");
		}
		this.name = name;
		this.properties = new ConcurrentHashMap<String, String>(properties);
		this.users = new CopyOnWriteArraySet<Object>();
		this.parents = new CopyOnWriteArraySet<LogicalReader>();
		this.readers = readers;
		this.immutable = immutable;
		readers.addAll(readers);
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
			for (LogicalReader reader : readers) {
				reader.aquire(user);
			}
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
			for (LogicalReader reader : readers) {
				reader.growUp(this);
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
		lrSpec.setIsComposite(true);
		Properties props = new Properties();
		for (String key : properties.keySet()) {
			LRProperty prop = new LRProperty();
			prop.setName(key);
			prop.setValue(properties.get(key));
			props.getProperty().add(prop);
		}
		lrSpec.setProperties(props);
		LRSpec.Readers spec = new LRSpec.Readers();
		for (LogicalReader reader : readers) {
			spec.getReader().add(reader.getName());
		}
		lrSpec.setReaders(spec);
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
		try {
			users.remove(user);
			if (parents.isEmpty() && users.isEmpty()) {
				inUse = false;
			}
			for (LogicalReader reader : readers) {
				reader.release(user);
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
					properties.put(propertyName, propertyValue);
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
			if (!inUse) {
				if (!immutable) {
					this.properties.clear();
					for (String key : properties.keySet()) {
						this.properties.put(key, properties.get(key));
					}
					this.readers.clear();
					this.readers.addAll(readers);
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
	 * @see
	 * org.rifidi.edge.lr.CompositeLogicalReader#addReader(org.rifidi.edge.lr
	 * .LogicalReader)
	 */
	@Override
	public void addReader(LogicalReader reader) throws ReaderInUseException,
			ImmutableReaderException {
		updating.lock();
		try {
			if (inUse) {
				if (!immutable) {
					readers.add(reader);
					return;
				} else {
					throw new ImmutableReaderException("Reader is immutable.");
				}
			}
			throw new ReaderInUseException("Reader is in use.");
		} finally {
			updating.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.lr.CompositeLogicalReader#addReaders(java.util.Set)
	 */
	@Override
	public void addReaders(Set<LogicalReader> reader)
			throws ReaderInUseException, ImmutableReaderException {
		updating.lock();
		try {
			if (inUse) {
				if (!immutable) {
					this.readers.addAll(reader);
					return;
				} else {
					throw new ImmutableReaderException("Reader is immutable.");
				}
			}
			throw new ReaderInUseException("Reader is in use.");
		} finally {
			updating.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.lr.CompositeLogicalReader#removeReader(org.rifidi.edge
	 * .lr.LogicalReader)
	 */
	@Override
	public void removeReader(LogicalReader reader) throws ReaderInUseException,
			ImmutableReaderException {
		updating.lock();
		try {
			if (inUse) {
				if (!immutable) {
					readers.remove(reader);
					return;
				} else {
					throw new ImmutableReaderException("Reader is immutable.");
				}
			}
			throw new ReaderInUseException("Reader is in use.");
		} finally {
			updating.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.lr.CompositeLogicalReader#removeReaders(java.util.Set)
	 */
	@Override
	public void removeReaders(Set<LogicalReader> reader)
			throws ReaderInUseException, ImmutableReaderException {
		updating.lock();
		try {
			if (inUse) {
				if (!immutable) {
					this.readers.addAll(reader);
					return;
				} else {
					throw new ImmutableReaderException("Reader is immutable.");
				}
			}
			throw new ReaderInUseException("Reader is in use.");
		} finally {
			updating.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.lr.CompositeLogicalReader#setReaders(java.util.Set)
	 */
	@Override
	public void setReaders(Set<LogicalReader> reader)
			throws ReaderInUseException, ImmutableReaderException {
		updating.lock();
		try {
			if (inUse) {
				if (!immutable) {
					this.readers.clear();
					this.readers.addAll(reader);
					return;
				} else {
					throw new ImmutableReaderException("Reader is immutable.");
				}
			}
			throw new ReaderInUseException("Reader is in use.");
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
		return new HashSet<LogicalReader>(readers);
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
