package org.rifidi.edge.core.rmi.client.commandconfigurationstub;

import org.rifidi.edge.api.rmi.dto.CommandConfigFactoryDTO;
import org.rifidi.edge.api.rmi.services.CommandManagerService;
import org.rifidi.rmi.proxycache.cache.AbstractRMICommandObject;

/**
 * This remote call gets a CommandConfigFactoryDTO for a given readerFactoryID.
 * It returns null if a readerFactory with the given ID is not avaialable
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class CCGetCommandConfigFactory extends
		AbstractRMICommandObject<CommandConfigFactoryDTO, RuntimeException> {

	private String readerFactoryID;

	/**
	 * Constructor.
	 * 
	 * @param serverDescription
	 */
	public CCGetCommandConfigFactory(CCServerDescription serverDescription,
			String readerFactoryID) {
		super(serverDescription);
		this.readerFactoryID = readerFactoryID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.rmi.proxycache.cache.AbstractRMICommandObject#performRemoteCall
	 * (java.lang.Object)
	 */
	@Override
	protected CommandConfigFactoryDTO performRemoteCall(Object remoteObject)
			throws RuntimeException {
		CommandManagerService stub = (CommandManagerService) remoteObject;
		return stub.getCommandConfigFactory(readerFactoryID);
	}

}
