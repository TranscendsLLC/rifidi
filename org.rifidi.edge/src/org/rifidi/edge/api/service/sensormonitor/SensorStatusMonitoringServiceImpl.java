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
package org.rifidi.edge.api.service.sensormonitor;

import java.util.ArrayList;
import java.util.Set;

import org.rifidi.edge.api.service.RifidiAppEsperFactory;
import org.rifidi.edge.api.service.RifidiAppService;
import org.rifidi.edge.notification.SensorStatusEvent;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

/**
 * This is an implementation of the SensorStatusMonitorService.
 * 
 * @author Matthew Dean
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class SensorStatusMonitoringServiceImpl extends
		RifidiAppService<SensorStatusSubscriber> implements
		SensorStatusMonitoringService {

	/**
	 * Constructor
	 * 
	 * @param group
	 *            the group this application is a part of
	 * @param name
	 *            The name of the application
	 */
	public SensorStatusMonitoringServiceImpl(String group, String name) {
		super(group, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.api.service.sensormonitor.
	 * SensorStatusMonitoringService
	 * #subscribe(org.rifidi.edge.api.service
	 * .sensormonitor.SensorStatusSubscriber, java.util.Set)
	 */
	@Override
	public void subscribe(final SensorStatusSubscriber subscriber,
			Set<String> sensorsToMonitor) {
		ArrayList<String> sensorList = new ArrayList<String>();
		if (sensorsToMonitor != null) {
			sensorList.addAll(sensorsToMonitor);
		}
		RifidiAppEsperFactory esperFactory = new SensorStatusMonitorEsperFactory(
				sensorList);
		subscribe(subscriber, esperFactory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.api.service.RifidiAppService#createListener(
	 * org.rifidi.edge.api.service.RifidiAppSubscriber)
	 */
	@Override
	protected StatementAwareUpdateListener createListener(
			final SensorStatusSubscriber subscriber) {
		return new StatementAwareUpdateListener() {

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
		};
	}

}
