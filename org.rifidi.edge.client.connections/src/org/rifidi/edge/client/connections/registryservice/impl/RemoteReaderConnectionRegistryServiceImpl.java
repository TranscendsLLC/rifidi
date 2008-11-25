package org.rifidi.edge.client.connections.registryservice.impl;

import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.activemq.store.jdbc.adapter.OracleJDBCAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.client.connections.registryservice.RemoteReaderConnectionRegistryService;
import org.rifidi.edge.client.connections.registryservice.listeners.RemoteReaderConnectionRegistryListener;
import org.rifidi.edge.client.connections.remotereader.RemoteReader;
import org.rifidi.edge.client.connections.remotereader.RemoteReaderID;

/**
 * The implementation of the RemoteReaderRegistryService
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RemoteReaderConnectionRegistryServiceImpl implements
		RemoteReaderConnectionRegistryService {

	private Log logger = LogFactory
			.getLog(RemoteReaderConnectionRegistryServiceImpl.class);
	private HashMap<RemoteReaderID, RemoteReader> remoteReaders;
	private CopyOnWriteArraySet<RemoteReaderConnectionRegistryListener> listeners;

	public RemoteReaderConnectionRegistryServiceImpl() {
		this.remoteReaders = new HashMap<RemoteReaderID, RemoteReader>();
		this.listeners = new CopyOnWriteArraySet<RemoteReaderConnectionRegistryListener>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see RemoteReaderConnectionRegistryService#addListener(
	 * RemoteReaderConnectionRegistryListener)
	 */
	@Override
	public synchronized RemoteReaderID addReader(RemoteReader remoteReader) {

		this.remoteReaders.put(remoteReader.getID(), remoteReader);

		for (RemoteReaderConnectionRegistryListener l : listeners) {
			l.readerAdded(remoteReader);
		}

		return remoteReader.getID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.client.connections.edgeserver.registryservice.
	 * RemoteReaderConnectionRegistryService#removeRemoteReader(java.lang.Long)
	 */
	@Override
	public void removeRemoteReader(RemoteReaderID remoteReaderID) {
		RemoteReader rr = this.remoteReaders.remove(remoteReaderID);

		if (rr != null) {
			for (RemoteReaderConnectionRegistryListener l : listeners) {
				l.readerRemoved(rr);
			}
		}

	}

	@Override
	public synchronized RemoteReader getRemoteReader(
			RemoteReaderID remoteReaderID) {
		return this.remoteReaders.get(remoteReaderID);
	}

	@Override
	public void addListener(RemoteReaderConnectionRegistryListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void removeListener(RemoteReaderConnectionRegistryListener listener) {
		this.listeners.remove(listener);
	}

}
