package org.rifidi.edge.core.rmi.client.readerconfigurationstub;

import java.util.Set;

import org.rifidi.edge.api.rmi.dto.ReaderFactoryDTO;
import org.rifidi.edge.api.rmi.services.SensorManagerService;
import org.rifidi.rmi.proxycache.cache.AbstractRMICommandObject;

/**
 * This remote call returns a list of ReaderFactory IDs. This list corresponds
 * to the "reader plugins" that are available.
 * 
 * @author Kyle Neumeier - Kyle Neumeier
 */
public class RS_GetReaderFactories extends
		AbstractRMICommandObject<Set<ReaderFactoryDTO>, RuntimeException> {

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            the server description
	 */
	public RS_GetReaderFactories(RS_ServerDescription serverDescription) {
		super(serverDescription);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.rmi.proxycache.cache.AbstractRMICommandObject#performRemoteCall
	 * (java.lang.Object)
	 */
	@Override
	protected Set<ReaderFactoryDTO> performRemoteCall(Object remoteObject)
			throws RuntimeException {
		SensorManagerService stub = (SensorManagerService) remoteObject;
		return stub.getReaderFactories();
	}

}
