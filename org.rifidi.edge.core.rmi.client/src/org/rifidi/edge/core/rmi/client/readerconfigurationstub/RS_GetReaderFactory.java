
package org.rifidi.edge.core.rmi.client.readerconfigurationstub;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.rifidi.edge.api.rmi.ReaderStub;
import org.rifidi.edge.api.rmi.dto.ReaderFactoryDTO;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This remote method call gets the DTO of a ReaderFactory. It returns null if
 * one is not available for the supplied ID
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class RS_GetReaderFactory
		extends
		ServerDescriptionBasedRemoteMethodCall<ReaderFactoryDTO, RuntimeException> {

	/** The ID of the factory to get the DTO of */
	private String readerFactoryID;

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            The serverdescription to use
	 * @param readerFactoryID
	 *            The readerFactoryID to get the DTO of
	 */
	public RS_GetReaderFactory(RS_ServerDescription serverDescription,
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
	protected ReaderFactoryDTO performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		ReaderStub stub = (ReaderStub) remoteObject;
		return stub.getReaderFactory(readerFactoryID);
	}

}
