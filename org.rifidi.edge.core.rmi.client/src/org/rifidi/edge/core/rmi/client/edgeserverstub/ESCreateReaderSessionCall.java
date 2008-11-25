/*
 *  CreateReaderSessionCommand.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.core.rmi.client.edgeserverstub;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.rifidi.edge.core.exceptions.RifidiReaderInfoNotFoundException;
import org.rifidi.edge.core.rmi.client.sessionstub.valueobjects.ReaderInfoWrapper;
import org.rifidi.edge.core.rmi.readerconnection.EdgeServerStub;
import org.rifidi.rmi.utils.cache.ServerDescription;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This Command Object creates a ReaderSession
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 *
 */
public class ESCreateReaderSessionCall extends ServerDescriptionBasedRemoteMethodCall<Long, RifidiReaderInfoNotFoundException> {
	private String _readerInfo;

	/**
	 * 
	 * @param serverDescription The description of the RMI stub
	 * @param readerInfo The readerInfo to use when creating the Session
	 */
	public ESCreateReaderSessionCall(ServerDescription serverDescription, ReaderInfoWrapper readerInfo){
		super(serverDescription);
		_readerInfo = readerInfo.getString();
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.rmi.client.remotecall.AbstractRemoteMethodCall#performRemoteCall(java.rmi.Remote)
	 */
	@Override
	protected Long performRemoteCall(Remote remoteObject)
			throws RemoteException, RifidiReaderInfoNotFoundException {
		EdgeServerStub conreg = (EdgeServerStub)remoteObject;
		return conreg.createReaderSession(_readerInfo);
	}

}
