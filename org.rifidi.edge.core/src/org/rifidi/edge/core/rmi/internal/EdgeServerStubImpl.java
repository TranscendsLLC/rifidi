/**
 * 
 */
package org.rifidi.edge.core.rmi.internal;

import java.rmi.RemoteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.configuration.services.ConfigurationService;
import org.rifidi.edge.core.api.rmi.EdgeServerStub;

/**
 * This is the implementation for the Edge Server RMI Stub
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class EdgeServerStubImpl implements EdgeServerStub {

	/** The configuration service for the edge server */
	private ConfigurationService configurationService;
	/** The logger for this class */
	private static Log logger = LogFactory.getLog(EdgeServerStubImpl.class);

	@Override
	public void save() throws RemoteException {
		configurationService.storeConfiguration();
	}

	/**
	 * @param configurationService
	 *            the configurationService to set
	 */
	public void setConfigurationService(
			ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

}
