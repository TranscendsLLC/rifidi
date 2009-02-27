/**
 * 
 */
package org.rifidi.edge.core.rmi.client.readerconfigurationstub;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.rmi.utils.cache.ServerDescription;

/**
 * This is a server description for a ReaderConfigurationStub
 * 
 * @author Kyle Neumeier - kyl@pramari.com
 * 
 */
public class RCServerDescription extends ServerDescription {

	private static final Log logger = LogFactory
			.getLog(RCServerDescription.class);

	/**
	 * Constructor
	 * 
	 * @param serverIP
	 *            The IP address of the server
	 * @param serverPort
	 *            The RMI port that the ReaderConfigurationStub is exposed on
	 */
	public RCServerDescription(String serverIP, int serverPort) {
		super(serverIP, serverPort, "ReaderConfigurationStub");
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
