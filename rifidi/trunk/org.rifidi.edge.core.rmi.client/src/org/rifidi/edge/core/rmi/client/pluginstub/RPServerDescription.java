package org.rifidi.edge.core.rmi.client.pluginstub;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import org.rifidi.edge.core.rmi.api.readerconnection.ReaderPluginManagerStub;
import org.rifidi.rmi.utils.cache.ServerDescription;

/**
 * A server description for the Reader Plugin Manager RMI stubs. It is used to
 * look up the Remote stub in the RMI registry
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RPServerDescription extends ServerDescription {

	public RPServerDescription(String serverIP, int serverPort) {
		super(serverIP, serverPort, ReaderPluginManagerStub.class.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.rmi.utils.cache.ServerDescription#getRemoteObj(java.rmi.registry
	 * .Registry)
	 */
	@Override
	protected Remote getStub(Registry registry) throws AccessException,
			RemoteException, NotBoundException {
		return (Remote) registry.lookup(this.getStubName());
	}

}
