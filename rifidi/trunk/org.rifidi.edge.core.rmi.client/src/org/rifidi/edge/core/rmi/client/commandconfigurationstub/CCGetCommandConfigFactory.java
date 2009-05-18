
package org.rifidi.edge.core.rmi.client.commandconfigurationstub;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.rifidi.edge.core.api.rmi.CommandStub;
import org.rifidi.edge.core.api.rmi.dto.CommandConfigFactoryDTO;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This remote call gets a CommandConfigFactoryDTO for a given readerFactoryID.
 * It returns null if a readerFactory with the given ID is not avaialable
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class CCGetCommandConfigFactory
		extends
		ServerDescriptionBasedRemoteMethodCall<CommandConfigFactoryDTO, RuntimeException> {

	private String readerFactoryID;

	/**
	 * Constructor.  
	 * 
	 * @param serverDescription
	 */
	public CCGetCommandConfigFactory(CCServerDescription serverDescription,
			String readerFactoryID) {
		super(serverDescription);
		this.readerFactoryID = readerFactoryID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.rmi.utils.remotecall.AbstractRemoteMethodCall#performRemoteCall
	 * (java.rmi.Remote)
	 */
	@Override
	protected CommandConfigFactoryDTO performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		CommandStub stub = (CommandStub) remoteObject;
		return stub.getCommandConfigFactory(readerFactoryID);
	}
}
