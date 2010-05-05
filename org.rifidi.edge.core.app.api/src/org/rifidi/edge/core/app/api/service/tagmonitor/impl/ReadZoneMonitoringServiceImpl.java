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
package org.rifidi.edge.core.app.api.service.tagmonitor.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.rifidi.edge.core.app.api.RifidiApp;
import org.rifidi.edge.core.app.api.service.tagmonitor.ReadZone;
import org.rifidi.edge.core.app.api.service.tagmonitor.ReadZoneMonitoringService;
import org.rifidi.edge.core.app.api.service.tagmonitor.ReadZoneSubscriber;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

/**
 * This is an implementation of the ReadZoneMonitoringService. Subscibers can
 * use this class to be notified when tags arrive or depart from a read zone
 * 
 * @author Matthew Dean
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class ReadZoneMonitoringServiceImpl extends RifidiApp implements
		ReadZoneMonitoringService {

	/** The number of subscribers created so far */
	private Integer counter = 0;
	/** A map to keep up with subscribers and their esper statements */
	private Map<ReadZoneSubscriber, Set<EPStatement>> subscriberMap = new HashMap<ReadZoneSubscriber, Set<EPStatement>>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.app.api.RifidiApp#start()
	 */
	@Override
	public void start() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.app.api.service.monitoring.ReadZoneMonitoringService
	 * #
	 * subscribe(org.rifidi.edge.core.app.api.service.monitoring.ReadZoneSubscriber
	 * , java.util.Set, java.lang.Integer)
	 */
	@Override
	public void subscribe(final ReadZoneSubscriber subscriber,
			Set<ReadZone> readZones, Integer departureTime) {
		synchronized (subscriberMap) {

			// if already subscribed, return
			if (subscriberMap.containsKey(subscriber)) {
				return;
			}
			// create a new set of esper statementss
			Set<EPStatement> statements = new HashSet<EPStatement>();
			// create a new factory for creating esper statemetns
			ReadZoneMonitorEsperFactory factory = new ReadZoneMonitorEsperFactory(
					readZones, getCounter(), departureTime);
			// create the esper statemetns from strings
			for (String statement : factory.createStatements()) {
				statements.add(esperService.getProvider().getEPAdministrator()
						.createEPL(statement));
			}
			// create the esper query
			EPStatement query = esperService.getProvider().getEPAdministrator()
					.createEPL(factory.getQuery());
			statements.add(query);

			// create the listener
			query.addListener(new StatementAwareUpdateListener() {

				@Override
				public void update(EventBean[] arg0, EventBean[] arg1,
						EPStatement arg2, EPServiceProvider arg3) {

					// all additions
					if (arg0 != null) {
						for (EventBean b : arg0) {
							TagReadEvent tre = (TagReadEvent) b.getUnderlying();
							subscriber.tagArrived(tre);
						}
					}
					// all deletions
					if (arg1 != null) {
						for (EventBean b : arg1) {
							TagReadEvent tre = (TagReadEvent) b.getUnderlying();
							subscriber.tagDeparted(tre);
						}
					}

				}
			});
			subscriberMap.put(subscriber, statements);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.app.api.service.monitoring.ReadZoneMonitoringService
	 * #
	 * subscribe(org.rifidi.edge.core.app.api.service.monitoring.ReadZoneSubscriber
	 * )
	 */
	@Override
	public void subscribe(ReadZoneSubscriber subscriber) {
		subscribe(subscriber, new HashSet<ReadZone>(), 2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.app.api.service.monitoring.ReadZoneMonitoringService
	 * #unsubscribe(org.rifidi.edge.core.app.api.service.monitoring.
	 * ReadZoneSubscriber)
	 */
	@Override
	public synchronized void unsubscribe(ReadZoneSubscriber rzs) {
		synchronized (subscriberMap) {
			Set<EPStatement> statements = this.subscriberMap.remove(rzs);
			for (EPStatement statement : statements) {
				statement.destroy();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.app.api.RifidiApp#stop()
	 */
	@Override
	public void stop() {
		super.stop();

		// need to override the stop method so that we make sure to delete all
		// esper statements
		Set<ReadZoneSubscriber> subscribers = new HashSet<ReadZoneSubscriber>(
				this.subscriberMap.keySet());
		for (ReadZoneSubscriber subscriber : subscribers) {
			unsubscribe(subscriber);
		}
	}

	/*
	 * Increments and returns the counter.
	 */
	private Integer getCounter() {
		return counter++;
	}

}
