/**
 * 
 */
package org.rifidi.edge.esper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class EsperManagementServiceImpl implements EsperManagementService {
	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(EsperManagementServiceImpl.class);
	/** Instance of the esper service. */
	private EPServiceProvider epService;

	/**
	 * Constructor.
	 */
	public EsperManagementServiceImpl() {
		logger.info("EsperManagementServiceImpl created.");
		epService = EPServiceProviderManager.getDefaultProvider();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.esper.EsperManagementService#getProvider()
	 */
	@Override
	public EPServiceProvider getProvider() {
		return epService;
	}

}
