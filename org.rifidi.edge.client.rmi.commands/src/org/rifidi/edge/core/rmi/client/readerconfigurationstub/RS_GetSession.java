package org.rifidi.edge.core.rmi.client.readerconfigurationstub;

import org.rifidi.edge.api.rmi.dto.SessionDTO;
import org.rifidi.edge.api.rmi.services.SensorManagerService;
import org.rifidi.rmi.proxycache.cache.AbstractRMICommandObject;

/**
 * This Remote call gets the session with the given ID and remote reader ID. It
 * returns the DTO for a session
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class RS_GetSession extends
		AbstractRMICommandObject<SessionDTO, RuntimeException> {

	/** The ID of the reader where the session exists */
	private String readerID;
	/** The ID of the session to get */
	private String sessionID;

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            The ServerDescription to use
	 * @param readerID
	 *            The ID of the reader where the session exists
	 * @param sessionID
	 *            The ID of the session to get
	 */
	public RS_GetSession(RS_ServerDescription serverDescription,
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
	protected SessionDTO performRemoteCall(Object remoteObject)
			throws RuntimeException {
		SensorManagerService stub = (SensorManagerService) remoteObject;
		return stub.getSession(readerID, sessionID);
	}

}
