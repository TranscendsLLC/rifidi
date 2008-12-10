/*
 *  ServerDescription.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.rmi.utils.cache;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * This class was inspired by the three oreilly.com articles on RMI by William
 * Grosso.
 * 
 * The purpose of this class is to provide the information necessary to reach a
 * remote object stub (i.e. an RMI server).
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class ServerDescription {

	/**
	 * The IP address of the remote machine that has the RMI registry which
	 * contains the stub
	 */
	private String _serverIP;

	/**
	 * The port of the remote machine that has the RMI registry which contains
	 * the stub
	 */
	private int _serverPort;

	/**
	 * The name of the stub in the RMI registry
	 */
	private String _stubName;

	/**
	 * A unique ID for this ServerDescription
	 */
	private String _hashString;

	/**
	 * 
	 * @param serverIP
	 *            The IP address of the remote machine that has the RMI registry
	 *            which contains the stub
	 * @param serverPort
	 *            The port of the remote machine that has the RMI registry which
	 *            contains the stub
	 * @param stubName
	 *            The name of the stub in the RMI registry
	 */
	public ServerDescription(String serverIP, int serverPort, String stubName) {
		_serverIP = serverIP;
		_stubName = stubName;
		_serverPort = serverPort;
		_hashString = _serverIP + _stubName + String.valueOf(_serverPort);

	}

	public int hashCode() {
		return _hashString.hashCode();
	}

	public boolean equals(Object object) {
		if (!(object instanceof ServerDescription)) {
			return false;
		}
		ServerDescription otherServerDescription = (ServerDescription) object;
		return _hashString.equals(otherServerDescription.getHashstring());
	}

	/**
	 * 
	 * @return the unique string
	 */
	private String getHashstring() {
		return _hashString;
	}

	/**
	 * @return the IP address of the remote machine
	 */
	public String getServerIP() {
		return _serverIP;
	}

	/**
	 * 
	 * @return the port number of the remote machine
	 */
	public int getServerPort() {
		return _serverPort;
	}

	/**
	 * 
	 * @return The name of the remote stub in the RMI registry
	 */
	public String getStubName() {
		return _stubName;
	}

	protected Remote getStub() {
		Remote returnValue = null;
		try {
			Registry registry = LocateRegistry.getRegistry(_serverIP,
					_serverPort);
			return getStub(registry);
		} catch (Exception ignored) {
		}
		return returnValue;
	}

	/**
	 * This method returns the remote stub object from the RMI registry. It is
	 * implemented as an abstract method, because this OSGI bundle does not have
	 * access to the specific classes that are transmitted over RMI, and thus it
	 * cannot deserialize the objects. Most concrete implementations of this
	 * class will be able to implement this method with a call like this:
	 * 
	 * <code>return (Remote) registry.lookup(this.getStubName());</code>
	 * 
	 * @param registry
	 *            The remote RMI registry where the remote object is stored
	 * @return The remote object stub
	 * @throws AccessException
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	protected abstract Remote getStub(Registry registry)
			throws AccessException, RemoteException, NotBoundException;
}
