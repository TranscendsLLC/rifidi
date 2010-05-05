/*
 *  SensorStatusMonitoringServiceImpl.java
 *
 *  Created:	Mar 23, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.core.app.api.service.sensormonitor.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.rifidi.edge.core.app.api.RifidiApp;
import org.rifidi.edge.core.app.api.service.sensormonitor.SensorStatusMonitoringService;
import org.rifidi.edge.core.app.api.service.sensormonitor.SensorStatusSubscriber;
import org.rifidi.edge.core.services.notification.data.management.SensorStatusEvent;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class SensorStatusMonitoringServiceImpl extends RifidiApp implements
		SensorStatusMonitoringService {

	/** A map to keep up with subscribers and their esper statements */
	private Map<SensorStatusSubscriber, Set<EPStatement>> subscriberMap = new HashMap<SensorStatusSubscriber, Set<EPStatement>>();

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
	 * @seeorg.rifidi.edge.core.app.api.service.sensormonitor.
	 * SensorStatusMonitoringService
	 * #subscribe(org.rifidi.edge.core.app.api.service
	 * .sensormonitor.SensorStatusSubscriber, java.util.Set)
	 */
	@Override
	public void subscribe(final SensorStatusSubscriber subscriber,
			Set<String> sensorsToMonitor) {
		synchronized (subscriberMap) {
			// if already subscribed, return
			if (subscriberMap.containsKey(subscriber)) {
				return;
			}
			// create a new set of esper statementss
			Set<EPStatement> statements = new HashSet<EPStatement>();
			
			//put sensors to monitor into an arraylist
			ArrayList<String> sensors = new ArrayList<String>();
			if (sensorsToMonitor != null) {
				sensors.addAll(sensorsToMonitor);
			}
			// create the query
			EPStatement statement = esperService.getProvider()
					.getEPAdministrator().createEPL(getEsper(sensors));
			statements.add(statement);

			// add a listener to the query
			statement.addListener(new StatementAwareUpdateListener() {

				@Override
				public void update(EventBean[] arg0, EventBean[] arg1,
						EPStatement arg2, EPServiceProvider arg3) {
					if (arg0 != null) {
						for (EventBean bean : arg0) {
							Object event = bean.getUnderlying();
							if (event instanceof SensorStatusEvent) {
								subscriber
										.handleSensorStatusEvent((SensorStatusEvent) event);
							}
						}
					}

				}
			});

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.app.api.service.sensormonitor.
	 * SensorStatusMonitoringService
	 * #unsubscribe(org.rifidi.edge.core.app.api.service
	 * .sensormonitor.SensorStatusSubscriber)
	 */
	public void unsubscribe(SensorStatusSubscriber subscriber) {
		synchronized (subscriberMap) {
			Set<EPStatement> statements = this.subscriberMap.remove(subscriber);
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
		Set<SensorStatusSubscriber> subscribers = new HashSet<SensorStatusSubscriber>(
				this.subscriberMap.keySet());
		for (SensorStatusSubscriber subscriber : subscribers) {
			unsubscribe(subscriber);
		}
	}

	private String getEsper(ArrayList<String> readersToMonitor) {
		String statement = "select * from SensorStatusEvent";
		// build where clause
		if (!readersToMonitor.isEmpty()) {
			StringBuffer sb = new StringBuffer();
			sb.append(" where " + sensorFilter(readersToMonitor.get(0)));
			for (int i = 1; i < readersToMonitor.size(); i++) {
				sb.append(" or " + sensorFilter(readersToMonitor.get(i)));
			}
			statement = statement + sb.toString();
		}
		return statement;
	}
	
	private String sensorFilter(String sensor){
		return "sensorID=\'"+sensor+"\'";
	}
}
