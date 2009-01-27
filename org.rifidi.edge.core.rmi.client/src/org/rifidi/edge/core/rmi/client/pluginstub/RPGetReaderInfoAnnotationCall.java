/*
 *  GetReaderInfoAnnotation.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.core.rmi.client.pluginstub;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.rifidi.edge.core.api.exceptions.RifidiReaderInfoNotFoundException;
import org.rifidi.edge.core.rmi.api.readerconnection.ReaderPluginManagerStub;
import org.rifidi.rmi.utils.cache.ServerDescription;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * 
 * This command gets the annotations for the readerInfo of a plugin
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RPGetReaderInfoAnnotationCall
		extends
		ServerDescriptionBasedRemoteMethodCall<String, RifidiReaderInfoNotFoundException> {

	private String readerInfoClassName;

	/**
	 * 
	 * This command gets the annotations for the reader info of a plugin
	 * 
	 * @param serverDescription
	 *            The description of the RMI stub
	 * @param readerInfoClassName
	 *            The qualified class name of the readerInfoClass for the reader
	 *            plugin
	 */
	public RPGetReaderInfoAnnotationCall(ServerDescription serverDescription,
			String readerInfoClassName) {
		super(serverDescription);
		this.readerInfoClassName = readerInfoClassName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.client.remotecall.AbstractRemoteMethodCall#
	 * performRemoteCall(java.rmi.Remote)
	 */
	@Override
	protected String performRemoteCall(Remote remoteObject)
			throws RemoteException, RifidiReaderInfoNotFoundException {
		return ((ReaderPluginManagerStub) remoteObject)
				.getReaderInfoAnnotation(readerInfoClassName);

	}

}
