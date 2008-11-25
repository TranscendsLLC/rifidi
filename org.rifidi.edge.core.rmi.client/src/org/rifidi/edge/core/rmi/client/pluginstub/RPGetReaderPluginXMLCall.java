/*
 *  GetReaderPluginXML.java
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

import org.rifidi.edge.core.exceptions.RifidiReaderPluginXMLNotFoundException;
import org.rifidi.edge.core.rmi.client.pluginstub.valueobjects.ReaderPluginWrapper;
import org.rifidi.edge.core.rmi.readerconnection.ReaderPluginManagerStub;
import org.rifidi.rmi.utils.cache.ServerDescription;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * 
 * This command gets the reader plugin XML for a certain reader plugin
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RPGetReaderPluginXMLCall
		extends
		ServerDescriptionBasedRemoteMethodCall<ReaderPluginWrapper, RifidiReaderPluginXMLNotFoundException> {

	private String readerInfoClassName;

	/**
	 * 
	 * This command gets the reader plugin XML for a certain reader plugin
	 * 
	 * @param serverDescription
	 *            The description of the RMI stub
	 * @param readerInfoClassName
	 *            The qualified class name of the readerInfoClass for the reader
	 *            plugin
	 */
	public RPGetReaderPluginXMLCall(ServerDescription serverDescription,
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
	protected ReaderPluginWrapper performRemoteCall(Remote remoteObject)
			throws RemoteException, RifidiReaderPluginXMLNotFoundException {
		ReaderPluginManagerStub stub = (ReaderPluginManagerStub) remoteObject;
		String xml = stub.getReaderPluginXML(readerInfoClassName);
		return new ReaderPluginWrapper(xml);
	}

}
