/**
 * 
 */
package org.rifidi.edge.core.rmi.client.readerconfigurationstub;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.management.MBeanInfo;

import org.rifidi.edge.core.rmi.ReaderConfigurationStub;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This call gets an MBeanInfo object that describes the ReaderConfigurations
 * that are produced from this factory. This information can be used to
 * construct a new reader configuration
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RCGetReaderConfigDescription extends
		ServerDescriptionBasedRemoteMethodCall<MBeanInfo, RuntimeException> {

	/** The supplied readerConfiguraitonFactoryID */
	private String readerConfigurationFactoryID;

	/**
	 * 
	 * @param serverDescription
	 *            The serverdescription
	 * @param readerConfiguraitonFactoryID
	 *            the ID of the reader configuraiton Factory to get a
	 *            description of
	 */
	public RCGetReaderConfigDescription(RCServerDescription serverDescription,
			String readerConfiguraitonFactoryID) {
		super(serverDescription);
		this.readerConfigurationFactoryID = readerConfiguraitonFactoryID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.rmi.utils.remotecall.AbstractRemoteMethodCall#performRemoteCall
	 * (java.rmi.Remote)
	 */
	@Override
	protected MBeanInfo performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		ReaderConfigurationStub stub = (ReaderConfigurationStub) remoteObject;
		return stub
				.getReaderConfigurationDescription(readerConfigurationFactoryID);
	}

}
