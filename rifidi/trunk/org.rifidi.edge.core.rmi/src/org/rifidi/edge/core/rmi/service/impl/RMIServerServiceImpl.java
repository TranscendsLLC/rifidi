package org.rifidi.edge.core.rmi.service.impl;

import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnectionRegistry;
import org.rifidi.edge.core.rmi.readerconnection.impl.RemoteReaderConnectionRegistryImpl;
import org.rifidi.edge.core.rmi.service.RMIServerService;
import org.rifidi.services.registry.ServiceRegistry;

public class RMIServerServiceImpl implements RMIServerService {
	/**
	 * Log System
	 */
	private Log logger = LogFactory.getLog(RMIServerServiceImpl.class);

	// RMI Registry
	private Registry registry;
	// TODO Make port assignment more dynamic
	private int port = 1099;

	private RemoteReaderConnectionRegistry remoteSessionRegistry;

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

		// Create a new RemoteSessionRegistry
		remoteSessionRegistry = new RemoteReaderConnectionRegistryImpl();

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
			logger.debug("Binding to: "  + RemoteReaderConnectionRegistry.class.getName());
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

	@Override
	public void bindToRMI(Object o) {
		// TODO implement this method
		logger.error("not implemented");
	}

}
