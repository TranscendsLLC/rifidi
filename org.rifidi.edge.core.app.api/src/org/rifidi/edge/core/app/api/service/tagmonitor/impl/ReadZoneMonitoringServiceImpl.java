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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.rifidi.edge.core.app.api.service.RifidiAppService;
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
public class ReadZoneMonitoringServiceImpl extends
		RifidiAppService<ReadZoneSubscriber> implements
		ReadZoneMonitoringService {

	/**
	 * Constructor
	 * 
	 * @param group
	 *            the group this application is a part of
	 * @param name
	 *            The name of the application
	 */
	public ReadZoneMonitoringServiceImpl(String group, String name) {
		super(group, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.app.api.service.tagmonitor.ReadZoneMonitoringService
	 * #
	 * subscribe(org.rifidi.edge.core.app.api.service.tagmonitor.ReadZoneSubscriber
	 * , java.util.List, java.lang.Float, java.util.concurrent.TimeUnit)
	 */
	@Override
	public void subscribe(final ReadZoneSubscriber subscriber,
			List<ReadZone> readZones, Float departureTime, TimeUnit timeUnit) {
		ReadZoneMonitorEsperFactory esperFactory = new ReadZoneMonitorEsperFactory(
				readZones, getCounter(), departureTime, timeUnit);
		subscribe(subscriber, esperFactory);

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
		subscribe(subscriber, Collections.EMPTY_LIST, 2f, TimeUnit.SECONDS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.app.api.service.RifidiAppService#createListener(
	 * org.rifidi.edge.core.app.api.service.RifidiAppSubscriber)
	 */
	@Override
	protected StatementAwareUpdateListener createListener(
			final ReadZoneSubscriber subscriber) {
		return new StatementAwareUpdateListener() {

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
		};
	}

}
