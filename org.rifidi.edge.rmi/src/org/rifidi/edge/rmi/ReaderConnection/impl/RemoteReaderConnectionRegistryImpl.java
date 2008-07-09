package org.rifidi.edge.rmi.ReaderConnection.impl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.service.ReaderPluginService;
import org.rifidi.edge.core.readersession.ReaderSession;
import org.rifidi.edge.core.readersession.service.ReaderSessionService;
import org.rifidi.edge.rmi.ReaderConnection.RemoteReaderConnection;
import org.rifidi.edge.rmi.ReaderConnection.RemoteReaderConnectionRegistry;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class RemoteReaderConnectionRegistryImpl implements
		RemoteReaderConnectionRegistry {

	private Log logger = LogFactory
			.getLog(RemoteReaderConnectionRegistryImpl.class);

	private ReaderPluginService readerPluginService;
	private ReaderSessionService readerSessionService;

	private HashMap<RemoteReaderConnection, RemoteReaderConnectionImpl> remoteSessionList = new HashMap<RemoteReaderConnection, RemoteReaderConnectionImpl>();

	public RemoteReaderConnectionRegistryImpl() {
		ServiceRegistry.getInstance().service(this);
	}

	@Override
	public RemoteReaderConnection createReaderConnection(ReaderInfo readerInfo)
			throws RemoteException {

		ReaderSession readerSession = readerSessionService
				.createReaderSesssion(readerInfo);
		RemoteReaderConnectionImpl remoteReaderConnection = new RemoteReaderConnectionImpl(
				readerSession);

		RemoteReaderConnection remoteReaderConnectionStub = null;
		try {
			remoteReaderConnectionStub = (RemoteReaderConnection) UnicastRemoteObject
					.exportObject(remoteReaderConnection, 0);
		} catch (RemoteException e) {
			e.printStackTrace();
			logger.error("Coudn't create RMI Stub for RemoteReaderConnection:",
					e);
			return null;
		}

		remoteSessionList.put(remoteReaderConnectionStub,
				remoteReaderConnection);

		return remoteReaderConnectionStub;
	}

	@Override
	public void deleteReaderConnection(
			RemoteReaderConnection remoteReaderConnection)
			throws RemoteException {
		RemoteReaderConnectionImpl remoteConnection = remoteSessionList
				.get(remoteReaderConnection);
		readerSessionService.destroyReaderSession(remoteConnection
				.getReaderSession());
	}

	@Override
	public List<RemoteReaderConnection> getAllReaderConnections()
			throws RemoteException {
		return new ArrayList<RemoteReaderConnection>(remoteSessionList.keySet());
	}

	@Override
	public List<String> getAvailableReaderPlugins() throws RemoteException {
		ArrayList<String> retVal = new ArrayList<String>(readerPluginService
				.getAllReaderPlugins());
		return retVal;
	}

	public ReaderPluginService getReaderPluginService() {
		return readerPluginService;
	}

	public void setReaderPluginService(ReaderPluginService readerPluginService) {
		this.readerPluginService = readerPluginService;
	}

	@Inject
	public ReaderSessionService getReaderSessionService() {
		return readerSessionService;
	}

	@Inject
	public void setReaderSessionService(
			ReaderSessionService readerSessionService) {
		this.readerSessionService = readerSessionService;
	}

}
