package org.rifidi.edge.core.rmi.service.impl;

import java.rmi.AccessException;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.rmi.service.RMIServerService;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * This is the implementation of a RMIServer. It will be a Service opening a RMI
 * port and allow Clients to connect to it. By default it only binds the
 * RemoteReaderRegistry into the RMI Namespace. This allows the Client to
 * create, delete and get a list of available RemoteReaderConnections.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class RMIServerServiceImpl implements RMIServerService {
	/**
	 * Log System
	 */
	private Log logger = LogFactory.getLog(RMIServerServiceImpl.class);

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

		// System.getProperties().put("java.rmi.server.logCalls","true");

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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.rmi.service.RMIServerService#stop()
	 */
	@Override
	public void stop() {
		try {

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
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.rmi.service.RMIServerService#bindToRMI(java.lang
	 * .Object)
	 */
	@Override
	public void bindToRMI(UnicastRemoteObject obj, String id)
			throws RemoteException {
		try {
			registry.rebind(id, obj);
			logger.debug("Remote object bound to RMI: " + id);
		} catch (AccessException e) {
			logger.error("Access exception");
			throw e;
		} catch (RemoteException e) {
			logger.error("Cannot bind object");
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.rmi.service.RMIServerService#unbindFromRMI(java.
	 * lang.String)
	 */
	@Override
	public void unbindFromRMI(String id) throws RemoteException {
		UnicastRemoteObject remoteObj = null;
		try {
			remoteObj = (UnicastRemoteObject) registry.lookup(id);
			UnicastRemoteObject.unexportObject(remoteObj, true);
			registry.unbind(id);
			logger.debug("Remote object unbound from RMI: " + id);
		} catch (AccessException e) {
			logger.error("Access exception");
			throw e;
		} catch (NoSuchObjectException e) {
			logger.error("Cannot unexport object");
		} catch (RemoteException e) {
			logger.error("Cannot remove object from registry");
			throw e;
		} catch (NotBoundException e) {
			logger.error("No object bound to ID: " + id);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.rmi.service.RMIServerService#lookup(java.lang.String
	 * )
	 */
	@Override
	public Remote lookup(String id) {
		try {
			return registry.lookup(id);
		} catch (AccessException e) {
			logger
					.error("AccessException when looking up object with ID "
							+ id);
			return null;
		} catch (RemoteException e) {
			logger.error("Remote Exception when looking up object with ID "
					+ id);
			return null;
		} catch (NotBoundException e) {
			logger.error("Remote Object with ID " + id
					+ " not bound to RMI registry");
			return null;
		}
	}

}
