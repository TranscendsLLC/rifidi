package org.rifidi.edge.core.rmi.readerconnection.impl;

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
import org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection;
import org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnectionRegistry;
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
	private HashMap<ReaderSession, RemoteReaderConnection> readerSessionSync = new HashMap<ReaderSession, RemoteReaderConnection>();

	public RemoteReaderConnectionRegistryImpl() {
		ServiceRegistry.getInstance().service(this);
	}

	@Override
	public RemoteReaderConnection createReaderConnection(ReaderInfo readerInfo)
			throws RemoteException {

		ReaderSession readerSession = readerSessionService
				.createReaderSession(readerInfo);

		return saveRemoteConnection(readerSession);
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
		for(ReaderSession session : readerSessionService.getAllReaderSessions())
		{
			if(!readerSessionSync.containsKey(session))
			{
				saveRemoteConnection(session);
			}
		}
		return new ArrayList<RemoteReaderConnection>(remoteSessionList.keySet());
	}

	@Override
	public List<String> getAvailableReaderPlugins() throws RemoteException {
		ArrayList<String> retVal = new ArrayList<String>(readerPluginService
				.getAllReaderInfos());
		return retVal;
	}

	@Inject
	public void setReaderPluginService(ReaderPluginService readerPluginService) {
		this.readerPluginService = readerPluginService;
	}

	@Inject
	public void setReaderSessionService(
			ReaderSessionService readerSessionService) {
		this.readerSessionService = readerSessionService;
	}

	private RemoteReaderConnection saveRemoteConnection(
			ReaderSession readerSession) {
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
		readerSessionSync.put(readerSession, remoteReaderConnection);
		return remoteReaderConnectionStub;
	}
}
