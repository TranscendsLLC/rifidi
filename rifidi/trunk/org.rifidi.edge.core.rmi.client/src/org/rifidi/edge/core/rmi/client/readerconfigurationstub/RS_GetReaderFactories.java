
package org.rifidi.edge.core.rmi.client.readerconfigurationstub;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

import org.rifidi.edge.core.api.rmi.ReaderStub;
import org.rifidi.edge.core.api.rmi.dto.ReaderFactoryDTO;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This remote call returns a list of ReaderFactory IDs. This list corresponds
 * to the "reader plugins" that are available.
 * 
 * @author Kyle Neumeier - Kyle Neumeier
 */
public class RS_GetReaderFactories
		extends
		ServerDescriptionBasedRemoteMethodCall<Set<ReaderFactoryDTO>, RuntimeException> {

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            the server description
	 */
	public RS_GetReaderFactories(RS_ServerDescription serverDescription) {
		super(serverDescription);
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.rmi.utils.remotecall.AbstractRemoteMethodCall#performRemoteCall(java.rmi.Remote)
	 */
	@Override
	protected Set<ReaderFactoryDTO> performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		ReaderStub stub = (ReaderStub) remoteObject;
		return stub.getReaderFactories();
	}

}
