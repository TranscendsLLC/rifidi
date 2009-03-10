/**
 * 
 */
package org.rifidi.edge.core.rmi.client.commandconfigurationstub;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import org.rifidi.rmi.utils.cache.ServerDescription;

/**
 * The ServerDescription for the CommandStub
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class CCServerDescription extends ServerDescription {

	/**
	 * Constructor
	 * 
	 * @param serverIP
	 *            The IP address of the server
	 * @param serverPort
	 *            The RMI port of the server
	 */
	public CCServerDescription(String serverIP, int serverPort) {
		super(serverIP, serverPort, "CommandConfiguraitonStub");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.rmi.utils.cache.ServerDescription#getStub(java.rmi.registry
	 * .Registry)
	 */
	@Override
	protected Remote getStub(Registry registry) throws AccessException,
			RemoteException, NotBoundException {
		return (Remote) registry.lookup(this.getStubName());
	}

}
