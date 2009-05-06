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
 * This method sets properties on a specified Reader. The return value is not
 * currenlty used, and it returns null
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RS_SetReaderProperties extends
		ServerDescriptionBasedRemoteMethodCall<Object, RuntimeException> {

	/** The new properties to set on the reader */
	private AttributeList attributes;
	/** The ID of the reader configuration */
	private String readerID;

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            The SeverdDescription to use
	 * @param readerID
	 *            The ID of the Reader
	 * @param readerProperties
	 *            The properties to set on the ReaderConfiguration
	 */
	public RS_SetReaderProperties(RS_ServerDescription serverDescription,
			String readerID, AttributeList readerProperties) {
		super(serverDescription);
		this.attributes = readerProperties;
		this.readerID = readerID;
	}

	@Override
	protected AttributeList performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		ReaderStub stub = (ReaderStub) remoteObject;
		stub.setReaderProperties(readerID, attributes);
		return null;
	}

}
