
package org.rifidi.edge.esper;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.messages.TagReadEvent;
import org.rifidi.edge.core.services.esper.EsperManagementService;
import org.rifidi.edge.esper.events.DestroyEvent;
import org.rifidi.edge.esper.events.StartEvent;
import org.rifidi.edge.esper.events.StopEvent;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;

/**
 * Service that configures and manages the esper provider.
 * 
 * @author Jochen Mader - jochen@pramari.com
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
		Configuration config = new Configuration();
		config.addEventType("StopEvent", StopEvent.class);
		config.addEventType("StartEvent", StartEvent.class);
		config.addEventType("DestroyEvent", DestroyEvent.class);
		config.addEventType("TagReadEvent", TagReadEvent.class);
		epService = EPServiceProviderManager.getDefaultProvider(config);
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
