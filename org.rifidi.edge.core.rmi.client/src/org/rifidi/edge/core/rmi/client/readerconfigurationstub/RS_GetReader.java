package org.rifidi.edge.core.rmi.client.readerconfigurationstub;

import org.rifidi.edge.api.rmi.dto.ReaderDTO;
import org.rifidi.edge.api.rmi.services.SensorManagerService;
import org.rifidi.rmi.proxycache.cache.AbstractRMICommandObject;

/**
 * This method gets the Reader Data Transfer Object for a specified Reader
 * 
 * @author Kyle Neumeier -kyle@pramari.com
 */
public class RS_GetReader extends
		AbstractRMICommandObject<ReaderDTO, RuntimeException> {

	/** The ID of the ReaderConfiguration to get properties of */
	private String readerID;

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            The ServerDescription to use
	 * @param readerID
	 *            The ID of the Reader to get the properties of
	 */
	public RS_GetReader(RS_ServerDescription serverDescription, String readerID) {
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
	protected ReaderDTO performRemoteCall(Object remoteObject)
			throws RuntimeException {
		SensorManagerService stub = (SensorManagerService) remoteObject;
		return stub.getReader(this.readerID);
	}

}
