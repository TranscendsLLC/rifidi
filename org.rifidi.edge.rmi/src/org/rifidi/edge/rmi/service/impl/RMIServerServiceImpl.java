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
import org.rifidi.edge.core.connection.registry.ReaderConnectionRegistryService;
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

	/* (non-Javadoc)
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
			// TODO Log4J debugging message
			logger.warn("RMI is already bound. Try to recieve instance!");
			// if yes get registry
			try {
				registry = LocateRegistry.getRegistry("127.0.0.1", port);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		// Create a new RemoteSessionRegistry
		remoteSessionRegistry = new RemoteReaderConnectionRegistryImpl(
				sessionRegistryService);

		RemoteReaderConnectionRegistry stub = null;
		try {
			stub = (RemoteReaderConnectionRegistry) UnicastRemoteObject
					.exportObject(remoteSessionRegistry, 0);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Bind the RemoteSessionRegistry to RMI
		logger.debug("Bind RemoteReaderConnectionRegistry to RMI Registry");
		try {
			registry.bind(RemoteReaderConnectionRegistry.class.getName(), stub);
		} catch (AccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AlreadyBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// For Debug use only. List all registered Objects in rmi registry
		// for (String value : registry.list()) {
		// System.out.println(value);
		// }
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.rmi.service.RMIServerService#stop()
	 */
	@Override
	public void stop() {
		try {
			logger.debug("Unbinding RemoteReaderConnectionRegistry");
			registry.unbind(RemoteReaderConnectionRegistry.class.getName());
			// TODO find out how to release the RMI TCP Socket again
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

}
