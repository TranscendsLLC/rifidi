package org.rifidi.edge.core.rmi.client.edgeserverstub;

import org.rifidi.edge.api.rmi.services.EdgeServerManagerService;
import org.rifidi.rmi.proxycache.cache.AbstractRMICommandObject;

/**
 * This call gets the timestamp of when the server started up
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class ESMS_GetStartupTimestampCommand extends
		AbstractRMICommandObject<Long, RuntimeException> {

	/**
	 * Constructor.
	 * 
	 * @param serverDescription
	 *            the server description to use
	 */
	public ESMS_GetStartupTimestampCommand(ESMS_ServerDescription serverDescription) {
		super(serverDescription);
	}

	/* (non-Javadoc)
	 * @see org.rifidi.rmi.proxycache.cache.AbstractRMICommandObject#performRemoteCall(java.lang.Object)
	 */
	@Override
	protected Long performRemoteCall(Object remoteObject)
			throws RuntimeException {
		return ((EdgeServerManagerService)remoteObject).getStartupTime();
	}
	
	
	
}
