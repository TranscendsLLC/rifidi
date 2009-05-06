/**
 * 
 */
package org.rifidi.edge.core.rmi.client.readerconfigurationstub;
//TODO: Comments
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

import org.rifidi.edge.core.api.rmi.ReaderStub;
import org.rifidi.edge.core.api.rmi.dto.ReaderDTO;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This Remote call returns the reader configurations that are available on the
 * server. It reaturns a Data Transfer Object that contains all the information
 * about the Readers
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RS_GetReaders
		extends
		ServerDescriptionBasedRemoteMethodCall<Set<ReaderDTO>, RuntimeException> {

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            The ServerDescription to use
	 */
	public RS_GetReaders(RS_ServerDescription serverDescription) {
		super(serverDescription);
	}

	@Override
	protected Set<ReaderDTO> performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		ReaderStub stub = (ReaderStub) remoteObject;
		return stub.getReaders();
	}

}
