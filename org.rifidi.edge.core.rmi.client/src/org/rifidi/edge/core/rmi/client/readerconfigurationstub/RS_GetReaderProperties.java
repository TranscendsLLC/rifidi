/**
 * 
 */
package org.rifidi.edge.core.rmi.client.readerconfigurationstub;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.management.AttributeList;

import org.rifidi.edge.core.api.rmi.ReaderStub;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This method gets the Attributes for a specified Reader
 * 
 * @author Kyle Neumeier -kyle@pramari.com
 * 
 */
public class RS_GetReaderProperties extends
		ServerDescriptionBasedRemoteMethodCall<AttributeList, RemoteException> {

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
	public RS_GetReaderProperties(RS_ServerDescription serverDescription,
			String readerID) {
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
	protected AttributeList performRemoteCall(Remote remoteObject)
			throws RemoteException, RemoteException {
		ReaderStub stub = (ReaderStub) remoteObject;
		return stub.getReaderProperties(this.readerID);
	}

}
