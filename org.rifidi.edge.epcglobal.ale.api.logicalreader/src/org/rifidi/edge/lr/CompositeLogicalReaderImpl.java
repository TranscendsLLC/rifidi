/**
 * 
 */
package org.rifidi.edge.lr;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.rifidi.edge.epcglobal.ale.api.lr.data.LRProperty;
import org.rifidi.edge.epcglobal.ale.api.lr.data.LRSpec;
import org.rifidi.edge.epcglobal.ale.api.lr.data.LRSpec.Properties;
import org.rifidi.edge.epcglobal.ale.api.lr.ws.ImmutableReaderExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.lr.ws.InUseExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.lr.ws.ReaderLoopExceptionResponse;

/**
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

	/**
	 * Constructor.
	 * 
	 * @param immutable
	 * @param name
	 * @param properties
	 */
	public CompositeLogicalReaderImpl(Boolean immutable, String name,
			Map<String, String> properties, Set<LogicalReader> readers) {
		this.name = name;
		this.properties = new ConcurrentHashMap<String, String>(properties);
		this.users = new HashSet<Object>();
		this.readers = new HashSet<LogicalReader>();
		readers.addAll(readers);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.lr.LogicalReader#aquire(java.lang.Object)
	 */
	@Override
	public void aquire(Object user) {
		synchronized (users) {
			users.add(user);
			for (LogicalReader reader : readers) {
				reader.aquire(user);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.lr.LogicalReader#destroy()
	 */
	@Override
	public void destroy() throws ImmutableReaderExceptionResponse,
			InUseExceptionResponse {
		if(isImmutable()){
			throw new ImmutableReaderExceptionResponse("Reader is immutable.");
		}
		if (!users.isEmpty()) {
			throw new InUseExceptionResponse("There are " + users.size()
					+ " users using the reader.");
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
		synchronized (users) {
			return new HashSet<Object>(users);
		}
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
		return !users.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.lr.LogicalReader#release(java.lang.Object)
	 */
	@Override
	public void release(Object user) {
		synchronized (users) {
			users.remove(user);
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
		if (!isInUse()) {
			if (!isImmutable()) {
				properties.put(propertyName, propertyValue);
				return;
			} else {
				throw new ImmutableReaderExceptionResponse(
						"Reader is immutable.");
			}
		}
		throw new InUseExceptionResponse("Reader is in use.");
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
		synchronized (users) {
			if (!isInUse()) {
				if (!isImmutable()) {
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
		}
		throw new InUseExceptionResponse("Reader is in use.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.lr.CompositeLogicalReader#addReader(org.rifidi.edge.lr
	 * .LogicalReader)
	 */
	@Override
	public void addReader(LogicalReader reader)
			throws ReaderLoopExceptionResponse, InUseExceptionResponse,
			ImmutableReaderExceptionResponse {
		synchronized (users) {
			if (!isInUse()) {
				if (!isImmutable()) {
					readers.add(reader);
					return;
				} else {
					throw new ImmutableReaderExceptionResponse(
							"Reader is immutable.");
				}
			}
		}
		throw new InUseExceptionResponse("Reader is in use.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.lr.CompositeLogicalReader#addReaders(java.util.Set)
	 */
	@Override
	public void addReaders(Set<LogicalReader> reader)
			throws ReaderLoopExceptionResponse, InUseExceptionResponse,
			ImmutableReaderExceptionResponse {
		synchronized (users) {
			if (!isInUse()) {
				if (!isImmutable()) {
					this.readers.addAll(reader);
					return;
				} else {
					throw new ImmutableReaderExceptionResponse(
							"Reader is immutable.");
				}
			}
		}
		throw new InUseExceptionResponse("Reader is in use.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.lr.CompositeLogicalReader#removeReader(org.rifidi.edge
	 * .lr.LogicalReader)
	 */
	@Override
	public void removeReader(LogicalReader reader)
			throws InUseExceptionResponse, ImmutableReaderExceptionResponse {
		synchronized (users) {
			if (!isInUse()) {
				if (!isImmutable()) {
					readers.remove(reader);
					return;
				} else {
					throw new ImmutableReaderExceptionResponse(
							"Reader is immutable.");
				}
			}
		}
		throw new InUseExceptionResponse("Reader is in use.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.lr.CompositeLogicalReader#removeReaders(java.util.Set)
	 */
	@Override
	public void removeReaders(Set<LogicalReader> reader)
			throws InUseExceptionResponse, ImmutableReaderExceptionResponse {
		synchronized (users) {
			if (!isInUse()) {
				if (!isImmutable()) {
					this.readers.addAll(reader);
					return;
				} else {
					throw new ImmutableReaderExceptionResponse(
							"Reader is immutable.");
				}
			}
		}
		throw new InUseExceptionResponse("Reader is in use.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.lr.CompositeLogicalReader#setReaders(java.util.Set)
	 */
	@Override
	public void setReaders(Set<LogicalReader> reader)
			throws ReaderLoopExceptionResponse, InUseExceptionResponse,
			ImmutableReaderExceptionResponse {
		synchronized (users) {
			if (!isInUse()) {
				if (!isImmutable()) {
					this.readers.clear();
					this.readers.addAll(reader);
					return;
				} else {
					throw new ImmutableReaderExceptionResponse(
							"Reader is immutable.");
				}
			}
		}
		throw new InUseExceptionResponse("Reader is in use.");
	}

}
