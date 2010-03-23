/*
 *  ReaderUptimeMonitoringServiceImpl.java
 *
 *  Created:	Mar 23, 2010
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

import org.rifidi.edge.core.app.api.ReaderUptimeMonitoringService;
import org.rifidi.edge.core.app.api.ReaderUptimeSubscriber;
import org.rifidi.edge.core.app.api.events.DowntimeEvent;
import org.rifidi.edge.core.app.api.events.ReaderEvent;
import org.rifidi.edge.core.app.api.events.UptimeEvent;
import org.rifidi.edge.core.services.esper.EsperManagementService;
import org.rifidi.edge.core.services.notification.data.management.SessionDownEvent;
import org.rifidi.edge.core.services.notification.data.management.SessionUpEvent;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class ReaderUptimeMonitoringServiceImpl implements
		ReaderUptimeMonitoringService {

	/** All statements that have been defined so far */
	private final Set<EPStatement> statements = new CopyOnWriteArraySet<EPStatement>();

	private Map<ReaderUptimeSubscriber, Set<String>> subscriberMap = new HashMap<ReaderUptimeSubscriber, Set<String>>();

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
		EPStatement queryDownTimeEvent = esperService.getProvider()
				.getEPAdministrator().createEPL(
						"select * from SessionDownEvent");
		statements.add(queryDownTimeEvent);

		EPStatement queryUpTimeEvent = esperService.getProvider()
				.getEPAdministrator().createEPL("select * from SessionUpEvent");
		statements.add(queryUpTimeEvent);

		StatementAwareUpdateListener stateUpdateListener = getSessionStateUpdateListener();
		queryDownTimeEvent.addListener(stateUpdateListener);
		queryUpTimeEvent.addListener(stateUpdateListener);
	}

	private StatementAwareUpdateListener getSessionStateUpdateListener() {
		return new StatementAwareUpdateListener() {

			@Override
			public void update(EventBean[] arg0, EventBean[] arg1,
					EPStatement arg2, EPServiceProvider arg3) {
				if (arg0 != null) {
					for (EventBean b : arg0) {
						if (b.getUnderlying() instanceof SessionUpEvent) {
							SessionUpEvent sue = (SessionUpEvent) b
									.getUnderlying();
							for(ReaderUptimeSubscriber rus:subscriberMap.keySet()) {
								for(String readerID:subscriberMap.get(rus)) {
									if(sue.getReaderID().equals(readerID)) {
										ReaderEvent re = new UptimeEvent();
										re.setReaderID(sue.getReaderID());
										re.setTime(sue.getTimestamp());
										rus.handleUptimeEvent(re);
									}
								}
							}
						} else if (b.getUnderlying() instanceof SessionDownEvent) {
							SessionDownEvent sde = (SessionDownEvent) b
									.getUnderlying();
							for(ReaderUptimeSubscriber rus:subscriberMap.keySet()) {
								for(String readerID:subscriberMap.get(rus)) {
									if(sde.getReaderID().equals(readerID)) {
										ReaderEvent re = new DowntimeEvent();
										re.setReaderID(sde.getReaderID());
										re.setTime(sde.getTimestamp());
										rus.handleUptimeEvent(re);
									}
								}
							}
						}
					}
				}
			}
		};
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
	 * org.rifidi.edge.core.app.api.ReaderUptimeMonitoringService#subscribe(
	 * org.rifidi.edge.core.app.api.ReaderUptimeSubscriber)
	 */
	@Override
	public boolean subscribe(ReaderUptimeSubscriber rus,
			Set<String> readersToSubscribe) {
		this.subscriberMap.put(rus, readersToSubscribe);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.app.api.ReaderUptimeMonitoringService#unsubscribe
	 * (org.rifidi.edge.core.app.api.ReaderUptimeSubscriber)
	 */
	@Override
	public boolean unsubscribe(ReaderUptimeSubscriber rus) {
		return false;
	}

}
