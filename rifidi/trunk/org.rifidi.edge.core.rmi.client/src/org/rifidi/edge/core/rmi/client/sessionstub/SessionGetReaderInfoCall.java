/*
 *  SessionGetReaderInfo.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.core.rmi.client.sessionstub;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.rifidi.edge.core.api.exceptions.RifidiReaderInfoNotFoundException;
import org.rifidi.edge.core.rmi.api.readerconnection.ReaderSessionStub;
import org.rifidi.edge.core.rmi.client.sessionstub.valueobjects.ReaderInfoWrapper;
import org.rifidi.rmi.utils.cache.ServerDescription;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * 
 * This command object returns the reader info for this session
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class SessionGetReaderInfoCall
		extends
		ServerDescriptionBasedRemoteMethodCall<ReaderInfoWrapper, RifidiReaderInfoNotFoundException> {

	/**
	 * This command object returns the reader info for this session
	 * 
	 * @param serverDescription
	 *            The description of the RMI stub
	 */
	public SessionGetReaderInfoCall(ServerDescription serverDescription) {
		super(serverDescription);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.rmi.utils.remotecall.AbstractRemoteMethodCall#performRemoteCall
	 * (java.rmi.Remote)
	 */
	@Override
	protected ReaderInfoWrapper performRemoteCall(Remote remoteObject)
			throws RemoteException, RifidiReaderInfoNotFoundException {
		String xml = ((ReaderSessionStub) remoteObject).getReaderInfo();
		return new ReaderInfoWrapper(xml);
	}

}
