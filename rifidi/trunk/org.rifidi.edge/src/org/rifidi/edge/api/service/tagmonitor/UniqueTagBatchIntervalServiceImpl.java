/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.api.service.tagmonitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.rifidi.edge.api.service.RifidiAppService;
import org.rifidi.edge.notification.TagReadEvent;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

/**
 * The implementation of the UniqueTagBatchIntervalService
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class UniqueTagBatchIntervalServiceImpl extends
		RifidiAppService<UniqueTagBatchIntervalSubscriber> implements
		UniqueTagBatchIntervalService {

	/**
	 * @param group
	 * @param name
	 */
	public UniqueTagBatchIntervalServiceImpl(String group, String name) {
		super(group, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.api.service.tagmonitor.UniqueTagBatchIntervalService
	 * #subscribe(org.rifidi.edge.api.service.tagmonitor.
	 * UniqueTagBatchIntervalSubscriber, java.util.List, java.lang.Float,
	 * java.util.concurrent.TimeUnit)
	 */
	@Override
	public void subscribe(UniqueTagBatchIntervalSubscriber subscriber,
			List<ReadZone> readZones, Float notifyInterval, TimeUnit timeUnit) {
		List<ReadZone> zones = new ArrayList<ReadZone>();
		if (readZones != null) {
			zones.addAll(readZones);
		}
		UniqueTagBatchIntervalEsperFactory esperFactory = new UniqueTagBatchIntervalEsperFactory(
				zones, notifyInterval, timeUnit, super.getCounter());
		super.subscribe(subscriber, esperFactory);
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
			final UniqueTagBatchIntervalSubscriber subscriber) {
		return new StatementAwareUpdateListener() {

			@Override
			public void update(EventBean[] arg0, EventBean[] arg1,
					EPStatement arg2, EPServiceProvider arg3) {
				Set<TagReadEvent> tags = new HashSet<TagReadEvent>();

				// look at tags being removed from the window
				if (arg1 != null) {
					for (EventBean bean : arg1) {
						tags.add((TagReadEvent) bean.getUnderlying());
					}
					subscriber.tagBatchSeen(tags);
				} else if (arg0 == null) {
					// if no tags are being removed from the window, then return
					// an empty set
					subscriber.tagBatchSeen(tags);
				}

			}
		};
	}

}
