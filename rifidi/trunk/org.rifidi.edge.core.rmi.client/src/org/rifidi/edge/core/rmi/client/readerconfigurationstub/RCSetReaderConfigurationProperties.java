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
 * This method sets properties on a specified ReaderConfiguration. It returns
 * the properties that were set on the reader and their values
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RCSetReaderConfigurationProperties extends
		ServerDescriptionBasedRemoteMethodCall<AttributeList, RuntimeException> {

	/** The new properties to set on the reader configuration */
	private AttributeList attributes;
	/** The ID of the reader configuration */
	private String readerConfigurationID;

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            The SeverdDescription to use
	 * @param readerConfigurationID
	 *            The ID of the ReaderConfiguration
	 * @param readerConfigurationProperties
	 *            The properties to set on the ReaderConfiguration
	 */
	public RCSetReaderConfigurationProperties(
			RCServerDescription serverDescription,
			String readerConfigurationID,
			AttributeList readerConfigurationProperties) {
		super(serverDescription);
		this.attributes = readerConfigurationProperties;
		this.readerConfigurationID = readerConfigurationID;
	}

	@Override
	protected AttributeList performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		ReaderStub stub = (ReaderStub) remoteObject;
		return stub.setReaderProperties(readerConfigurationID,
				attributes);
	}

}
