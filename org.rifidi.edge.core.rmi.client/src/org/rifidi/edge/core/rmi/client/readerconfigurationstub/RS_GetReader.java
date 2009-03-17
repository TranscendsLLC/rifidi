/**
 * 
 */
package org.rifidi.edge.core.rmi.client.readerconfigurationstub;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.rifidi.edge.core.api.rmi.ReaderStub;
import org.rifidi.edge.core.api.rmi.dto.ReaderDTO;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This method gets the Reader Data Transfer Object for a specified Reader
 * 
 * @author Kyle Neumeier -kyle@pramari.com
 * 
 */
public class RS_GetReader extends
		ServerDescriptionBasedRemoteMethodCall<ReaderDTO, RuntimeException> {

	/** The ID of the ReaderConfiguration to get properties of */
	private String readerID;

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            The ServerDescription to use
	 * @param readerID
	 *            The ID of the Reader to get the properties of
	 */
	public RS_GetReader(RS_ServerDescription serverDescription, String readerID) {
		super(serverDescription);
		this.readerID = readerID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.rmi.utils.remotecall.AbstractRemoteMethodCall#performRemoteCall
	 * (java.rmi.Remote)
	 */
	@Override
	protected ReaderDTO performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		ReaderStub stub = (ReaderStub) remoteObject;
		return stub.getReader(this.readerID);
	}

}
