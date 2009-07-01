package org.rifidi.edge.core.rmi.client.readerconfigurationstub;

import org.rifidi.edge.api.rmi.services.SensorManagerService;
import org.rifidi.rmi.proxycache.cache.AbstractRMICommandObject;

/**
 * This command stops a currently executing session on the reader. It returns
 * null
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class RS_StopSession extends
		AbstractRMICommandObject<Object, RuntimeException> {

	/** The ID of the reader */
	private String readerID;
	/** The ID of the session */
	private String sessionID;

	/**
	 * Constructor.
	 * 
	 * @param serverDescription
	 * @param readerID
	 * @param sessionID
	 */
	public RS_StopSession(RS_ServerDescription serverDescription,
			String readerID, String sessionID) {
		super(serverDescription);
		this.readerID = readerID;
		this.sessionID = sessionID;
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
		stub.stopSession(readerID, sessionID);
		return null;
	}

}
