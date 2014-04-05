/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.api.service.tagmonitor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.rifidi.edge.api.service.RifidiAppEsperFactory;
import org.rifidi.edge.api.service.RifidiAppService;
import org.rifidi.edge.notification.TagReadEvent;

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
	 * @see org.rifidi.edge.api.service.tagmonitor.ReadZoneMonitoringService #
	 * subscribe(org.rifidi.edge.api.service.tagmonitor.ReadZoneSubscriber ,
	 * java.util.List, java.lang.Float, java.util.concurrent.TimeUnit)
	 */
	@Override
	public void subscribe(final ReadZoneSubscriber subscriber,
			List<ReadZone> readZones, Float departureTime, TimeUnit timeUnit) {
		this.subscribe(subscriber, readZones, departureTime, timeUnit, false, false);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.service.tagmonitor.ReadZoneMonitoringService #
	 * subscribe(org.rifidi.edge.api.service.tagmonitor.ReadZoneSubscriber ,
	 * java.util.List, java.lang.Float, java.util.concurrent.TimeUnit)
	 */
	@Override
	public void subscribe(final ReadZoneSubscriber subscriber,
			List<ReadZone> readZones, Float departureTime, TimeUnit timeUnit, boolean unique) {
		this.subscribe(subscriber, readZones, departureTime, timeUnit, unique, false);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.api.service.tagmonitor.ReadZoneMonitoringService#subscribe(org.rifidi.edge.api.service.tagmonitor.ReadZoneSubscriber, java.util.List, java.lang.Float, java.util.concurrent.TimeUnit, boolean, boolean)
	 */
	@Override
	public void subscribe(ReadZoneSubscriber subscriber,
			List<ReadZone> readZones, Float departureTime, TimeUnit timeUnit,
			boolean wholereader, boolean useRegex) {
		RifidiAppEsperFactory esperFactory;
		if (wholereader) {
			esperFactory = new UniqueReadZoneMonitorEsperFactory(
					readZones, getCounter(), departureTime, timeUnit, true, true);
		} else {
			esperFactory = new ReadZoneMonitorEsperFactory(
					readZones, getCounter(), departureTime, timeUnit, useRegex);
		}
		subscribe(subscriber, esperFactory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.service.monitoring.ReadZoneMonitoringService #
	 * subscribe(org.rifidi.edge.api.service.monitoring.ReadZoneSubscriber )
	 */
	@Override
	public void subscribe(ReadZoneSubscriber subscriber) {
		subscribe(subscriber, Collections.EMPTY_LIST, 2f, TimeUnit.SECONDS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.service.RifidiAppService#createListener(
	 * org.rifidi.edge.api.service.RifidiAppSubscriber)
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.service.tagmonitor.ReadZoneMonitoringService #
	 * subscribe(org.rifidi.edge.api.service.tagmonitor.ReadZoneSubscriber ,
	 * org.rifidi.edge.api.service.tagmonitor.ReadZone, java.lang.Float,
	 * java.util.concurrent.TimeUnit)
	 */
	@Override
	public void subscribe(ReadZoneSubscriber subscriber, ReadZone readZone,
			Float departureTime, TimeUnit timeUnit) {
		List<ReadZone> tempList = new LinkedList<ReadZone>();
		tempList.add(readZone);
		this.subscribe(subscriber, tempList, departureTime, timeUnit);
	}

}
