package org.rifidi.edge.core.rmi.client.edgeserverstub;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.rmi.EdgeServerStub;
import org.rifidi.rmi.utils.cache.ServerDescription;

/**
 * A server description for the EdgeServer RMI stubs. It is used to look up the
 * Remote stub in the RMI registry
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ESServerDescription extends ServerDescription {

	Log logger = LogFactory.getLog(ESServerDescription.class);
	
	public ESServerDescription(String serverIP, int serverPort) {
		super(serverIP, serverPort, EdgeServerStub.class.getName());
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
