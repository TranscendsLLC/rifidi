/**
 * 
 */
package org.rifidi.edge.core.rmi.client.edgeserverstub;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.rifidi.edge.core.api.rmi.EdgeServerStub;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This call gets the timestamp of when the server started up
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ESGetStartupTimestamp extends
		ServerDescriptionBasedRemoteMethodCall<Long, RuntimeException> {

	/**
	 * @param serverDescription
	 *            the server description to use
	 */
	public ESGetStartupTimestamp(ESServerDescription serverDescription) {
		super(serverDescription);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.rmi.utils.remotecall.AbstractRemoteMethodCall#performRemoteCall
	 * (java.rmi.Remote)
	 */
	@Override
	protected Long performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		EdgeServerStub stub = (EdgeServerStub) remoteObject;
		return stub.getStartupTime();
	}

}
