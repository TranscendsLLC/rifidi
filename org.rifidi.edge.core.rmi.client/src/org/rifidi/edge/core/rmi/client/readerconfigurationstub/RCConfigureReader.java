package org.rifidi.edge.core.rmi.client.readerconfigurationstub;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.management.AttributeList;

import org.rifidi.edge.core.api.ReaderStub;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This call will cause the ReaderConfiguration to make a connection to the
 * reader and configure it with the properties in the ReaderConfiguration as
 * well as get "read only" property values from it. It returns all the
 * properties and values of the ReaderConfiguration
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RCConfigureReader extends
		ServerDescriptionBasedRemoteMethodCall<AttributeList, RuntimeException> {

	/** The ID of the ReaderConfiguration to configure */
	private String readerConfigurationID;

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            The ServerDescription to use
	 * @param readerConfigurationID
	 *            the ID of the ReaderConfiguration to configure
	 */
	public RCConfigureReader(RCServerDescription serverDescription,
			String readerConfigurationID) {
		super(serverDescription);
		this.readerConfigurationID = readerConfigurationID;
	}

	@Override
	protected AttributeList performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		ReaderStub stub = (ReaderStub) remoteObject;
		return stub.configureReader(readerConfigurationID);
	}

}
