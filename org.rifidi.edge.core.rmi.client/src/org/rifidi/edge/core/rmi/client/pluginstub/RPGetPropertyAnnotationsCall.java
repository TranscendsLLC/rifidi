/*
 *  GetPropertyAnnotations.java
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

import org.rifidi.dynamicswtforms.xml.exceptions.DynamicSWTFormAnnotationException;
import org.rifidi.edge.core.api.exceptions.RifidiException;
import org.rifidi.edge.core.rmi.api.readerconnection.ReaderPluginManagerStub;
import org.rifidi.edge.core.rmi.api.readerconnection.exceptions.RifidiPluginDoesNotExistException;
import org.rifidi.edge.core.rmi.client.pluginstub.valueobjects.FormAnnotationListWrapper;
import org.rifidi.rmi.utils.cache.ServerDescription;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * 
 * This command gets the annotations for the properties of a plugin
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RPGetPropertyAnnotationsCall
		extends
		ServerDescriptionBasedRemoteMethodCall<FormAnnotationListWrapper, RifidiException> {

	private String readerInfoClassName;

	/**
	 * 
	 * This command gets the annotations for the properties of a plugin
	 * 
	 * @param serverDescription
	 *            The description of the RMI stub
	 * @param readerInfoClassName
	 *            The qualified class name of the readerInfoClass for the reader
	 *            plugin
	 */
	public RPGetPropertyAnnotationsCall(ServerDescription serverDescription,
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
	protected FormAnnotationListWrapper performRemoteCall(Remote remoteObject)
			throws RemoteException, RifidiException {
		try {
			String xml = ((ReaderPluginManagerStub) remoteObject)
					.getPropertyAnnotations(readerInfoClassName);
			return new FormAnnotationListWrapper(xml);
		} catch (RifidiPluginDoesNotExistException e) {
			throw new RifidiException(e);
		} catch (DynamicSWTFormAnnotationException e) {
			throw new RifidiException(e);
		}
	}

}
