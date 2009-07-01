package org.rifidi.edge.core.rmi.client.edgeserverstub;

import org.rifidi.edge.api.rmi.services.EdgeServerManagerService;
import org.rifidi.rmi.proxycache.cache.AbstractRMICommandObject;

/**
 * This call saves the current properties of all configurations (e.g.
 * ReaderConfiguration, CommandConfiguration) to the configuration file
 * 
 * makeCall() currently returns a null object
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class ESMS_SaveCommand extends AbstractRMICommandObject<Object, RuntimeException> {

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            The ServerDescription to use
	 */
	public ESMS_SaveCommand(ESMS_ServerDescription serverDescription) {
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
	protected Object performRemoteCall(Object remoteObject)
			throws RuntimeException {
		((EdgeServerManagerService) remoteObject).save();
		return null;
	}

}
