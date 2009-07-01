package org.rifidi.edge.core.rmi.client.readerconfigurationstub;

import org.rifidi.edge.api.rmi.services.SensorManagerService;
import org.rifidi.rmi.proxycache.cache.AbstractRMICommandObject;

/**
 * This call deletes a reader that is currently on the server. The return type
 * is not currently used and will return null
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class RS_DeleteReader extends
		AbstractRMICommandObject<Object, RuntimeException> {

	/** The ID of the ReaderConfiguration to delete */
	private String readerID;

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            The ServerDescription to use
	 * @param readerID
	 *            the ID of the Reader to delete
	 */
	public RS_DeleteReader(RS_ServerDescription serverDescription,
			String readerID) {
		super(serverDescription);
		this.readerID = readerID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.rmi.proxycache.cache.AbstractRMICommandObject#performRemoteCall
	 * (java.lang.Object)
	 */
	@Override
	protected Object performRemoteCall(Object remoteObject)
			throws RuntimeException {
		SensorManagerService stub = (SensorManagerService) remoteObject;
		stub.deleteReader(this.readerID);
		return null;
	}

}
