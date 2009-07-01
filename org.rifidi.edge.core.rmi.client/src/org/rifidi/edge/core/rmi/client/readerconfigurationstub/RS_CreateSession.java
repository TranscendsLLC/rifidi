package org.rifidi.edge.core.rmi.client.readerconfigurationstub;

import java.util.Set;

import org.rifidi.edge.api.rmi.dto.SessionDTO;
import org.rifidi.edge.api.rmi.services.SensorManagerService;
import org.rifidi.rmi.proxycache.cache.AbstractRMICommandObject;

/**
 * This command creates a new session on the reader. It retuns a List of
 * ReaderSession data transfer objects that give information about all the
 * sessions currently available on that reader
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class RS_CreateSession extends
		AbstractRMICommandObject<Set<SessionDTO>, RuntimeException> {

	/** The readerID to create a session on */
	private String readerID;

	/**
	 * Constructor.
	 * 
	 * @param serverDescription
	 */
	public RS_CreateSession(RS_ServerDescription serverDescription,
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
	protected Set<SessionDTO> performRemoteCall(Object remoteObject)
			throws RuntimeException {
		SensorManagerService stub = (SensorManagerService) remoteObject;
		return stub.createSession(readerID);
	}

}
