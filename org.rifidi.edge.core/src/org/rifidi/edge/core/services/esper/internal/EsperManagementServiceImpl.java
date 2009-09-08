/*
 * 
 * EsperManagementServiceImpl.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
package org.rifidi.edge.core.services.esper.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.services.esper.EsperManagementService;
import org.rifidi.edge.core.services.esper.events.DestroyEvent;
import org.rifidi.edge.core.services.esper.events.StartEvent;
import org.rifidi.edge.core.services.esper.events.StopEvent;
import org.rifidi.edge.core.services.notification.data.DatacontainerEvent;
import org.rifidi.edge.core.services.notification.data.EPCGeneration1Event;
import org.rifidi.edge.core.services.notification.data.EPCGeneration2Event;
import org.rifidi.edge.core.services.notification.data.ReadCycle;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

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

	private final Configuration config;

	/**
	 * Constructor.
	 */
	public EsperManagementServiceImpl() {
		logger.info("EsperManagementServiceImpl created.");
		config = new Configuration();
		config.addEventType("StopEvent", StopEvent.class);
		config.addEventType("StartEvent", StartEvent.class);
		config.addEventType("DestroyEvent", DestroyEvent.class);
		config.addEventType("TagReadEvent", TagReadEvent.class);
		config.addEventType("DatacontainerEvent", DatacontainerEvent.class);
		config.addEventType("EPCGeneration1Event", EPCGeneration1Event.class);
		config.addEventType("EPCGeneration2Event", EPCGeneration2Event.class);
		config.addEventType("ReadCycle", ReadCycle.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.services.esper.EsperManagementService#getProvider()
	 */
	@Override
	public EPServiceProvider getProvider() {
		return EPServiceProviderManager.getDefaultProvider(config);
	}

}
