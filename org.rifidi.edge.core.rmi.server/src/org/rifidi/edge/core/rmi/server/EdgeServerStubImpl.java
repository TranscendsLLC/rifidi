
package org.rifidi.edge.core.rmi.server;
import java.rmi.RemoteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.configuration.services.ConfigurationService;
import org.rifidi.edge.core.api.rmi.EdgeServerStub;

/**
 * This is the implementation for the Edge Server RMI Stub
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class EdgeServerStubImpl implements EdgeServerStub {

	/** The configuration service for the edge server */
	private ConfigurationService configurationService;
	/** The logger for this class */
	private static Log logger = LogFactory.getLog(EdgeServerStubImpl.class);
	/** The timestamp of when the edgeserver was started up */
	private Long startupDate;

	/**
	 * Constructor.  
	 */
	public EdgeServerStubImpl() {
		// TODO: this should probably really be the startup of the core plugin
		// and not the startup of the RMI plugin
		startupDate = System.currentTimeMillis();
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.core.api.rmi.EdgeServerStub#save()
	 */
	@Override
	public void save() throws RemoteException {
		logger.debug("RMI Call: Save");
		configurationService.storeConfiguration();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.api.rmi.EdgeServerStub#getStartupTime()
	 */
	@Override
	public Long getStartupTime() throws RemoteException {
		logger.debug("RMI Call: getStartupTime");
		return startupDate;
	}

	/**
	 * Sets the configuration service for this class.  
	 * 
	 * @param configurationService
	 *            the configurationService to set
	 */
	public void setConfigurationService(
			ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

}
