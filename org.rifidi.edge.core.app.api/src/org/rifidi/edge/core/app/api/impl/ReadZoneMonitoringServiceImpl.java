/*
 *  ReadZoneMonitoringServiceImpl.java
 *
 *  Created:	Mar 16, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.core.app.api.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.rifidi.edge.core.app.api.ReadZoneMonitoringService;
import org.rifidi.edge.core.app.api.ReadZoneSubscriber;
import org.rifidi.edge.core.app.api.util.ReadZoneEsperConstructor;
import org.rifidi.edge.core.services.esper.EsperManagementService;

import com.espertech.esper.client.EPStatement;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class ReadZoneMonitoringServiceImpl implements ReadZoneMonitoringService {
	
	/**
	 * 
	 */
	private Integer counter = 0;
	
	/** All statements that have been defined so far */
	private final Set<EPStatement> statements = new CopyOnWriteArraySet<EPStatement>();

	private Map<ReadZoneSubscriber, Set<String>> subscriberMap = new HashMap<ReadZoneSubscriber, Set<String>>();

	/** Esper service */
	private volatile EsperManagementService esperService;
	
	/**
	 * Called by spring
	 * 
	 * @param esperService
	 *            the esperService to set
	 */
	public void setEsperService(EsperManagementService esperService) {
		this.esperService = esperService;
	}
	
	/**
	 * 
	 */
	public void start() {
	}

	/**
	 * 
	 */
	public void stop() {
		for (EPStatement statement : statements) {
			statement.destroy();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.app.api.ReadZoneMonitoringService#subscribe(org.
	 * rifidi.edge.core.app.api.ReadZoneSubscriber, java.util.Set)
	 */
	@Override
	public synchronized boolean subscribe(ReadZoneEsperConstructor rzec) {
		for(EPStatement eps:rzec.getStatements(this.esperService, this.getCounter())) {
			this.statements.add(eps);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.app.api.ReadZoneMonitoringService#unsubscribe(org
	 * .rifidi.edge.core.app.api.ReadZoneSubscriber)
	 */
	@Override
	public synchronized boolean unsubscribe(ReadZoneSubscriber rzs) {
		this.subscriberMap.remove(rzs);
		return true;
	}
	
	/*
	 * Increments and returns the counter.  
	 */
	private Integer getCounter() {
		return counter++;
	}

}
