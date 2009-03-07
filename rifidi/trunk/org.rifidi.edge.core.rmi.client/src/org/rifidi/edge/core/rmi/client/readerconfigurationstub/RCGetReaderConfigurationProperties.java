/**
 * 
 */
package org.rifidi.edge.core.rmi.client.readerconfigurationstub;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.management.AttributeList;

import org.rifidi.edge.core.api.ReaderStub;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This method gets the Attributes for a specified ReaderConfiguration
 * 
 * @author Kyle Neumeier -kyle@pramari.com
 * 
 */
public class RCGetReaderConfigurationProperties extends
		ServerDescriptionBasedRemoteMethodCall<AttributeList, RemoteException> {

	/** The ID of the ReaderConfiguration to get properties of */
	private String readerConfigurationID;

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            The ServerDescription to use
	 * @param readerConfigurationID
	 *            The ID of the ReaderConfiguraiton to get the properties of
	 */
	public RCGetReaderConfigurationProperties(
			RCServerDescription serverDescription, String readerConfigurationID) {
		super(serverDescription);
		this.readerConfigurationID = readerConfigurationID;
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
		return stub
				.getReaderProperties(this.readerConfigurationID);
	}

}
