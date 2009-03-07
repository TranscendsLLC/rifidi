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
 * This call creates a new ReaderConfiguraiton on the server.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RCCreateReaderConfiguration extends
		ServerDescriptionBasedRemoteMethodCall<String, RuntimeException> {

	/** The supplied readerConfigurationID to use */
	private String readerConfigurationFactoryID;
	/** The attributes to set on the new reader configuration */
	private AttributeList attributes;

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            The server description to use
	 * @param readerConfigurationFactoryID
	 *            The ID of the ReaderConfigurationFactory to use (i.e. the kind
	 *            of reader configuration to create)
	 * @param readerConfigurationProperties
	 *            the properties to set on the new reader configuration.
	 *            Properties of the reader configuration not included in this
	 *            list will have their default values.
	 *            readerConfigurationProperties may contains no Attributes
	 *            inside, but it may not be null
	 */
	public RCCreateReaderConfiguration(RCServerDescription serverDescription,
			String readerConfigurationFactoryID,
			AttributeList readerConfigurationProperties) {
		super(serverDescription);
		this.readerConfigurationFactoryID = readerConfigurationFactoryID;
		this.attributes = readerConfigurationProperties;
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
		return stub.createReader(readerConfigurationFactoryID,
				attributes);
	}

}
