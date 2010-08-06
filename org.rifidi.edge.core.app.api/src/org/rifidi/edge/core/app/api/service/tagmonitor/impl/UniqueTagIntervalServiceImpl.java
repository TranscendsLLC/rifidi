/**
 * 
 */
package org.rifidi.edge.core.app.api.service.tagmonitor.impl;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.rifidi.edge.core.app.api.service.RifidiAppEsperFactory;
import org.rifidi.edge.core.app.api.service.RifidiAppService;
import org.rifidi.edge.core.app.api.service.tagmonitor.ReadZone;
import org.rifidi.edge.core.app.api.service.tagmonitor.UniqueTagIntervalService;
import org.rifidi.edge.core.app.api.service.tagmonitor.UniqueTagIntervalSubscriber;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

/**
 * This is an implementation of the UniqueTagIntervalService
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class UniqueTagIntervalServiceImpl extends
		RifidiAppService<UniqueTagIntervalSubscriber> implements
		UniqueTagIntervalService {

	/**
	 * Constructor
	 * 
	 * @param group
	 *            the group this application is a part of
	 * @param name
	 *            The name of the application
	 */
	public UniqueTagIntervalServiceImpl(String group, String name) {
		super(group,name);
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
			final UniqueTagIntervalSubscriber subscriber) {
		return new StatementAwareUpdateListener() {

			@Override
			public void update(EventBean[] arg0, EventBean[] arg1,
					EPStatement arg2, EPServiceProvider arg3) {
				if (arg0 != null) {
					for (EventBean b : arg0) {
						TagReadEvent tag = (TagReadEvent) b.getUnderlying();
						subscriber.tagSeen(tag);
					}
				}

			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.app.api.service.tagmonitor.
	 * UniqueTagIntervalService
	 * #subscribe(org.rifidi.edge.core.app.api
	 * .service.tagmonitor.UniqueTagIntervalNotificationSubscriber,
	 * java.util.List, java.lang.Float, java.util.concurrent.TimeUnit)
	 */
	@Override
	public void subscribe(UniqueTagIntervalSubscriber subscriber,
			List<ReadZone> readZones, Float notifyInterval, TimeUnit timeUnit) {
		RifidiAppEsperFactory esperFactory = new UniqueTagIntervalEsperFactory(
				readZones, notifyInterval, timeUnit, getCounter());
		subscribe(subscriber, esperFactory);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.app.api.service.tagmonitor.
	 * UniqueTagIntervalService
	 * #subscribe(org.rifidi.edge.core.app.api
	 * .service.tagmonitor.UniqueTagIntervalNotificationSubscriber)
	 */
	@Override
	public void subscribe(UniqueTagIntervalSubscriber subscriber) {
		subscribe(subscriber, Collections.EMPTY_LIST, 5f, TimeUnit.SECONDS);

	}

}
