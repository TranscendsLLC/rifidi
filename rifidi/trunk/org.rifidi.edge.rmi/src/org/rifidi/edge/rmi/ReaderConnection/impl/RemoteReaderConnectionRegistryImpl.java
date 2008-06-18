package org.rifidi.edge.rmi.ReaderConnection.impl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.connection.IReaderConnection;
import org.rifidi.edge.core.connection.registry.ReaderConnectionRegistryService;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPluginService.ReaderPluginRegistryService;
import org.rifidi.edge.rmi.ReaderConnection.RemoteReaderConnection;
import org.rifidi.edge.rmi.ReaderConnection.RemoteReaderConnectionRegistry;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

public class RemoteReaderConnectionRegistryImpl implements
		RemoteReaderConnectionRegistry {

	private Log logger = LogFactory
			.getLog(RemoteReaderConnectionRegistryImpl.class);

	private ReaderConnectionRegistryService readerConnectionRegistryService;
	private ReaderPluginRegistryService readerPluginRegistryService;

	private HashMap<RemoteReaderConnection, RemoteReaderConnection> remoteSessionList = new HashMap<RemoteReaderConnection, RemoteReaderConnection>();

	public RemoteReaderConnectionRegistryImpl(
			ReaderConnectionRegistryService sessionRegistryService) {
		this.readerConnectionRegistryService = sessionRegistryService;
		ServiceRegistry.getInstance().service(this);
	}

	@Override
	public RemoteReaderConnection createReaderSession(
			AbstractReaderInfo connectionInfo) throws RemoteException {
		logger.debug("Remote Call: createReaderSession()");

		// Create ReaderConnection
		IReaderConnection readerConnection = readerConnectionRegistryService
				.createReaderConnection(connectionInfo);
		// Create RemoteReaderConnection
		RemoteReaderConnection remoteReaderConnection = new RemoteReaderConnectionImpl(
				readerConnection);

		// Create RMI Stub for RemoteReaderConnection
		RemoteReaderConnection remoteReaderConnectionStub = null;
		try {
			remoteReaderConnectionStub = (RemoteReaderConnection) UnicastRemoteObject
					.exportObject(remoteReaderConnection, 0);
		} catch (RemoteException e) {
			logger.error("Coudn't create RMI Stub for RemoteReaderConnection:",
					e);
			// e.printStackTrace();
		}

		remoteSessionList.put(remoteReaderConnectionStub,
				remoteReaderConnection);

		return remoteReaderConnectionStub;
	}

	@Override
	public void deleteReaderSession(
			RemoteReaderConnection remoteReaderConnection)
			throws RemoteException {
		logger.debug("Remote Call: deleteReaderSession()");
		RemoteReaderConnection readerToDelete = remoteSessionList
				.remove(remoteReaderConnection);
		readerConnectionRegistryService.deleteReaderConnection(Integer
				.parseInt(readerToDelete.getTagQueueName()));
	}

	@Override
	public List<String> getAvailableReaderAdapters() throws RemoteException {
		return readerPluginRegistryService.getAvailableReaderAdapters();
	}

	@Override
	public List<RemoteReaderConnection> getAllSessions() throws RemoteException {
		return new ArrayList<RemoteReaderConnection>(remoteSessionList.values());
	}

	@Inject
	public void setReaderAdapterRegistryService(
			ReaderPluginRegistryService readerPluginRegistryService) {
		this.readerPluginRegistryService = readerPluginRegistryService;
	}

}
