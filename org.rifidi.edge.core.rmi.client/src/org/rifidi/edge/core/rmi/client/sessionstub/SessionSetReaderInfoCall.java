/*
 *  SessionSetReaderInfoCall.java
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

import org.rifidi.edge.core.exceptions.RifidiReaderInfoNotFoundException;
import org.rifidi.edge.core.rmi.readerconnection.ReaderSessionStub;
import org.rifidi.rmi.utils.cache.ServerDescription;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This command object is used to set the ReaderInfo on this session
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class SessionSetReaderInfoCall
		extends
		ServerDescriptionBasedRemoteMethodCall<Object, RifidiReaderInfoNotFoundException> {

	private String readerInfo;

	/**
	 * This command object is used to set the ReaderInfo on this session
	 * 
	 * @param serverDescription
	 *            The description of the RMI stub
	 * @param readerInfo
	 *            The new readerInfo to set on this session. It must be in XML
	 */
	public SessionSetReaderInfoCall(ServerDescription serverDescription,
			String readerInfo) {
		super(serverDescription);
		this.readerInfo = readerInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.rmi.utils.remotecall.AbstractRemoteMethodCall#performRemoteCall
	 * (java.rmi.Remote)
	 */
	@Override
	protected Object performRemoteCall(Remote remoteObject)
			throws RemoteException, RifidiReaderInfoNotFoundException {
		((ReaderSessionStub) remoteObject).setReaderInfo(readerInfo);
		return null;
	}

}
