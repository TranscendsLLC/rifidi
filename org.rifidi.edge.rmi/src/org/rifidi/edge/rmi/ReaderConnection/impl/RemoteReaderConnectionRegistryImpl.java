/*
 *  RemoteReaderConnectionImpl.java
 *
 *  Created:	Jun 19, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.rmi.ReaderConnection.impl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.connection.IReaderConnection;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.service.readerconnection.ReaderConnectionRegistryService;
import org.rifidi.edge.core.service.readerplugin.ReaderPluginRegistryService;
import org.rifidi.edge.rmi.ReaderConnection.RemoteReaderConnection;
import org.rifidi.edge.rmi.ReaderConnection.RemoteReaderConnectionRegistry;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

public class RemoteReaderConnectionRegistryImpl implements
		RemoteReaderConnectionRegistry {

	/**
	 * Logging System
	 */
	private Log logger = LogFactory
			.getLog(RemoteReaderConnectionRegistryImpl.class);

	/**
	 * ReaderConnectionRegistry is a Service providing creation and storing of
	 * the ReaderConnections.
	 */
	private ReaderConnectionRegistryService readerConnectionRegistryService;

	/**
	 * ReaderPluginRegistryService is a Service providing Information about
	 * available ReaderPlugins. Needs to be serviced. See
	 * 
	 * @Inject statement.
	 */
	private ReaderPluginRegistryService readerPluginRegistryService;

	/**
	 * Store for all created RemoteReaderConnections. The first
	 * RemoteReaderConnection is holding the UnicastRemoteObject reference. The
	 * second RemoteReaderConnection is the instance of the
	 * RemoteReaderConnection associated to the UnicastRemoteObject.
	 */
	private HashMap<RemoteReaderConnection, RemoteReaderConnection> remoteSessionList = new HashMap<RemoteReaderConnection, RemoteReaderConnection>();

	/**
	 * Constructor
	 * 
	 * @param sessionRegistryService
	 */
	public RemoteReaderConnectionRegistryImpl(
			ReaderConnectionRegistryService sessionRegistryService) {
		this.readerConnectionRegistryService = sessionRegistryService;
		ServiceRegistry.getInstance().service(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.rmi.ReaderConnection.RemoteReaderConnectionRegistry#createReaderConnection(org.rifidi.edge.core.readerPlugin.AbstractReaderInfo)
	 */
	@Override
	public RemoteReaderConnection createReaderConnection(
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.rmi.ReaderConnection.RemoteReaderConnectionRegistry#deleteReaderConnection(org.rifidi.edge.rmi.ReaderConnection.RemoteReaderConnection)
	 */
	@Override
	public void deleteReaderConnection(
			RemoteReaderConnection remoteReaderConnection)
			throws RemoteException {
		logger.debug("Remote Call: deleteReaderSession()");
		RemoteReaderConnection readerToDelete = remoteSessionList
				.remove(remoteReaderConnection);
		readerConnectionRegistryService.deleteReaderConnection(Integer
				.parseInt(readerToDelete.getTagQueueName()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.rmi.ReaderConnection.RemoteReaderConnectionRegistry#getAvailableReaderAdapters()
	 */
	@Override
	public List<String> getAvailableReaderPlugins() throws RemoteException {
		return readerPluginRegistryService.getAvailableReaderAdapters();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.rmi.ReaderConnection.RemoteReaderConnectionRegistry#getAllSessions()
	 */
	@Override
	public List<RemoteReaderConnection> getAllReaderConnections()
			throws RemoteException {
		return new ArrayList<RemoteReaderConnection>(remoteSessionList.values());
	}

	/**
	 * Dependency Injecting. This method is needed to be serviced by the
	 * org.rifidi.services Framework
	 * 
	 * @param readerPluginRegistryService
	 */
	@Inject
	public void setReaderAdapterRegistryService(
			ReaderPluginRegistryService readerPluginRegistryService) {
		this.readerPluginRegistryService = readerPluginRegistryService;
	}

}
