
package org.rifidi.edge.core.rmi.client.readerconfigurationstub;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.management.MBeanInfo;

import org.rifidi.edge.core.api.rmi.ReaderStub;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This call gets an MBeanInfo object that describes the Readers that are
 * produced from this factory. This information can be used to construct a new
 * reader
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class RS_GetReaderDescription extends
		ServerDescriptionBasedRemoteMethodCall<MBeanInfo, RuntimeException> {

	/** The supplied readerConfiguraitonFactoryID */
	private String readerFactoryID;

	/**
	 * 
	 * @param serverDescription
	 *            The serverdescription
	 * @param readerFactoryID
	 *            the ID of the reader Factory to get a description of
	 */
	public RS_GetReaderDescription(RS_ServerDescription serverDescription,
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
	protected MBeanInfo performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		ReaderStub stub = (ReaderStub) remoteObject;
		return stub.getReaderDescription(readerFactoryID);
	}

}
