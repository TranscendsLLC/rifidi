/**
 * 
 */
package org.rifidi.edge.core.rmi.client.readerconfigurationstub;
//TODO: Comments
import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.management.AttributeList;

import org.rifidi.edge.core.api.rmi.ReaderStub;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This call creates a new Reader on the server.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RS_CreateReader extends
		ServerDescriptionBasedRemoteMethodCall<String, RuntimeException> {

	/** The supplied readerConfigurationID to use */
	private String readerFactoryID;
	/** The attributes to set on the new reader configuration */
	private AttributeList attributes;

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            The server description to use
	 * @param readerFactoryID
	 *            The ID of the ReaderFactory to use (i.e. the kind of reader to
	 *            create)
	 * @param readerProperties
	 *            the properties to set on the new reader. Properties of the
	 *            reader not included in this list will have their default
	 *            values. reader Properties may contains no Attributes inside,
	 *            but it may not be null
	 */
	public RS_CreateReader(RS_ServerDescription serverDescription,
			String readerFactoryID, AttributeList readerProperties) {
		super(serverDescription);
		this.readerFactoryID = readerFactoryID;
		this.attributes = readerProperties;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.rmi.utils.remotecall.AbstractRemoteMethodCall#performRemoteCall
	 * (java.rmi.Remote)
	 */
	@Override
	protected String performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		ReaderStub stub = (ReaderStub) remoteObject;
		return stub.createReader(readerFactoryID, attributes);
	}

}
