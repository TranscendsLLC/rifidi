/*
 *  RMIServerServiceImpl.java
 *
 *  Created:	Jun 19, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.rmi.service.impl;

import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.service.readerconnection.ReaderConnectionRegistryService;
import org.rifidi.edge.rmi.ReaderConnection.RemoteReaderConnectionRegistry;
import org.rifidi.edge.rmi.ReaderConnection.impl.RemoteReaderConnectionRegistryImpl;
import org.rifidi.edge.rmi.service.RMIServerService;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

public class RMIServerServiceImpl implements RMIServerService {

	/**
	 * Log System
	 */
	private Log logger = LogFactory.getLog(RMIServerServiceImpl.class);

	// SessionRegistry
	private ReaderConnectionRegistryService sessionRegistryService;
	private RemoteReaderConnectionRegistryImpl remoteSessionRegistry;

	// RMI Registry
	private Registry registry;
	// TODO Make port assignment more dynamic
	private int port = 1099;

	/**
	 * Default Constructor to get the class serviced
	 */
	public RMIServerServiceImpl() {
		ServiceRegistry.getInstance().service(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.rmi.service.RMIServerService#start()
	 */
	@Override
	public void start() {

		// Get the RMIRegistry and bind it to port and hostname
		// TODO try to use the Registry from JMS
		try {
			logger.debug("Starting RMI Service");
			// Test if there is already an regisrty
			registry = LocateRegistry.createRegistry(port);
		} catch (RemoteException e) {
			logger.warn("RMI is already bound. Try to recieve instance!");
			// if yes get registry
			try {
				registry = LocateRegistry.getRegistry("127.0.0.1", port);
			} catch (RemoteException e1) {
				logger.error(e1);
			}
		}

		if (sessionRegistryService == null) {
			logger.error("Could not obtain RemoteReaderConnectionRegsirtyImpl");
		}
		// Create a new RemoteSessionRegistry
		remoteSessionRegistry = new RemoteReaderConnectionRegistryImpl(
				sessionRegistryService);

		RemoteReaderConnectionRegistry stub = null;
		try {
			stub = (RemoteReaderConnectionRegistry) UnicastRemoteObject
					.exportObject(remoteSessionRegistry, 0);
		} catch (RemoteException e) {
			logger.error(e);
		}

		// Bind the RemoteSessionRegistry to RMI
		logger.debug("Bind RemoteReaderConnectionRegistry to RMI Registry");
		try {
			registry.bind(RemoteReaderConnectionRegistry.class.getName(), stub);
		} catch (AccessException e) {
			logger.error(e);
		} catch (RemoteException e) {
			logger.error(e);
		} catch (AlreadyBoundException e) {
			logger.error(e);
		}

		// For Debug use only. List all registered Objects in rmi registry
		// for (String value : registry.list()) {
		// System.out.println(value);
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.rmi.service.RMIServerService#stop()
	 */
	@Override
	public void stop() {
		try {
			logger.debug("Unbinding RemoteReaderConnectionRegistry");
			// Unbind the RemoteReaderConnections
			registry.unbind(RemoteReaderConnectionRegistry.class.getName());

			logger.debug("Releasing the RMI ServerSocket: "
					+ Registry.REGISTRY_PORT);
			// Remove the binding to the socket the rmi registry is using
			UnicastRemoteObject.unexportObject(registry, true);

			for (String object : registry.list()) {
				logger.warn("Object " + object
						+ " still remains in RMI Registry");
			}
		} catch (AccessException e) {
			logger.error("RMI Access violation");
			e.printStackTrace();
		} catch (RemoteException e) {
			logger.error("RMI RemoteException occured "
					+ "while trying to unbind RemoteSessionRegistry");
			e.printStackTrace();
		} catch (NotBoundException e) {
			logger.error("RemoteReaderConnectionRegistry was not "
					+ "found in RMI Registry while trying to unbind");
			e.printStackTrace();
		}
	}

	/**
	 * Dependency Injection. This method is needed by the rifidi services
	 * framework.
	 * 
	 * @param sessionRegistryService
	 */
	@Inject
	public void setSessionRegistryService(
			ReaderConnectionRegistryService sessionRegistryService) {
		logger.debug("Services: SessionRegistryService was injected");
		this.sessionRegistryService = sessionRegistryService;
	}

	@Override
	public void bindToRMI(Object o) {
		// TODO implement this method
		logger.error("not implemented");
	}

}
