package org.rifidi.edge.core.rmi.client.sessionstub;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import org.rifidi.rmi.utils.cache.ServerDescription;

/**
 * This is the session service description for a Reader Session. Sessions are
 * stored in the RMI registry by their sessionID
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class SessionServerDescription extends ServerDescription {

	/**
	 * 
	 * @param serverIP
	 *            The IP address of the RMI server
	 * @param serverPort
	 *            The port of the RMI server
	 * @param sessionID
	 *            The session ID of the reader session
	 */
	public SessionServerDescription(String serverIP, int serverPort,
			Long sessionID) {
		super(serverIP, serverPort, Long.toString(sessionID));
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
