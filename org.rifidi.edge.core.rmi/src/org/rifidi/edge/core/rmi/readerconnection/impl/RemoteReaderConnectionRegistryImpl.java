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
import org.rifidi.edge.core.readersession.service.ReaderSessionListener;
import org.rifidi.edge.core.readersession.service.ReaderSessionService;
import org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection;
import org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnectionRegistry;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * The implementation of the RemoteReaderConnectionRegistry. This is the Factory
 * to create new RemoteReaderConnections. It will be exported by RMI to allow
 * Clients to create and delete RemoteReaderConnections. It also allows to keep
 * track of currently running RemoteReaderSessions.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class RemoteReaderConnectionRegistryImpl implements
		RemoteReaderConnectionRegistry, ReaderSessionListener {

	private Log logger = LogFactory
			.getLog(RemoteReaderConnectionRegistryImpl.class);

	private ReaderPluginService readerPluginService;
	private ReaderSessionService readerSessionService;

	private HashMap<RemoteReaderConnection, RemoteReaderConnectionImpl> remoteSessionList = new HashMap<RemoteReaderConnection, RemoteReaderConnectionImpl>();
	private HashMap<ReaderSession, RemoteReaderConnection> readerSessionSync = new HashMap<ReaderSession, RemoteReaderConnection>();

	/**
	 * Constructor
	 */
	public RemoteReaderConnectionRegistryImpl() {
		ServiceRegistry.getInstance().service(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnectionRegistry
	 * #createReaderConnection(org.rifidi.edge.core.readerplugin.ReaderInfo)
	 */
	@Override
	public RemoteReaderConnection createReaderConnection(ReaderInfo readerInfo)
			throws RemoteException {
		logger.debug("Create Reader called");
		ReaderSession readerSession = readerSessionService
				.createReaderSession(readerInfo);

		return saveRemoteConnection(readerSession);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnectionRegistry
	 * #deleteReaderConnection
	 * (org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection)
	 */
	@Override
	public void deleteReaderConnection(
			RemoteReaderConnection remoteReaderConnection)
			throws RemoteException {
		RemoteReaderConnectionImpl remoteConnection = remoteSessionList
				.remove(remoteReaderConnection);
		readerSessionService.destroyReaderSession(remoteConnection
				.getReaderSession());
		readerSessionSync.remove(remoteConnection.getReaderSession());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnectionRegistry
	 * #getAllReaderConnections()
	 */
	@Override
	public List<RemoteReaderConnection> getAllReaderConnections()
			throws RemoteException {
		for (ReaderSession session : readerSessionService
				.getAllReaderSessions()) {
			if (!readerSessionSync.containsKey(session)) {
				saveRemoteConnection(session);
			}
		}
		return new ArrayList<RemoteReaderConnection>(remoteSessionList.keySet());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnectionRegistry
	 * #getAvailableReaderPlugins()
	 */
	@Override
	public List<String> getAvailableReaderPlugins() throws RemoteException {
		ArrayList<String> retVal = new ArrayList<String>(readerPluginService
				.getAllReaderInfos());
		return retVal;
	}

	/**
	 * Inject method to obtain a instance of the ReaderPluginService from the
	 * RegistryService Framework
	 * 
	 * @param readerPluginService
	 *            ReaderPluginService instance
	 */
	@Inject
	public void setReaderPluginService(ReaderPluginService readerPluginService) {
		this.readerPluginService = readerPluginService;
	}

	/**
	 * Inject method to obtain a instance of the ReaderSessionService from the
	 * RegistryService Framework
	 * 
	 * @param readerSessionService
	 *            ReaderSessionService
	 */
	@Inject
	public void setReaderSessionService(
			ReaderSessionService readerSessionService) {
		this.readerSessionService = readerSessionService;
		readerSessionService.addReaderSessionListener(this);
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

	@Override
	public void addEvent(ReaderSession readerSession) {	
	}

	@Override
	public void removeEvent(ReaderSession readerSession) {
		RemoteReaderConnection remoteReaderConnection = readerSessionSync.remove(readerSession);
		remoteSessionList.values().remove(remoteReaderConnection);
	}
}
