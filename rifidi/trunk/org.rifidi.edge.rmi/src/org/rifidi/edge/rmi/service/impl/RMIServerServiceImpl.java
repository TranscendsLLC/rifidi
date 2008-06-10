package org.rifidi.edge.rmi.service.impl;

import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.rifidi.edge.core.session.SessionRegistryService;
import org.rifidi.edge.rmi.service.RMIServerService;
import org.rifidi.edge.rmi.session.RemoteSessionRegistry;
import org.rifidi.edge.rmi.session.impl.RemoteSessionRegistryImpl;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

public class RMIServerServiceImpl implements RMIServerService {

	// SessionRegistry
	private SessionRegistryService sessionRegistryService;
	private RemoteSessionRegistryImpl remoteSessionRegistry;

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

	@Override
	public void start() throws RemoteException, AlreadyBoundException {
		// Get the RMIRegistry and bind it to port and hostname
		// TODO find out if localhost is sufficient
		registry = LocateRegistry.getRegistry(port);

		// Create a new RemoteSessionRegistry
		remoteSessionRegistry = new RemoteSessionRegistryImpl(
				sessionRegistryService);

		RemoteSessionRegistry stub = (RemoteSessionRegistry) UnicastRemoteObject
				.exportObject(remoteSessionRegistry, 0);

		// Bind the RemoteSessionRegistry to RMI
		registry.bind(RemoteSessionRegistry.class.getName(), stub);

	}

	@Override
	public void stop() {
		try {
			registry.unbind(RemoteSessionRegistry.class.getName());
			// TODO find out how to release the RMI TCP Socket again
		} catch (AccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param sessionRegistryService
	 */
	@Inject
	public void setSessionRegistryService(
			SessionRegistryService sessionRegistryService) {
		this.sessionRegistryService = sessionRegistryService;
	}

}
