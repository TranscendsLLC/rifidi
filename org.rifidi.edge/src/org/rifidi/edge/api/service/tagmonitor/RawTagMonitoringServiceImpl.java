/*******************************************************************************
 * Copyright (c) 2015 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
package org.rifidi.edge.api.service.tagmonitor;

import java.util.ArrayList;
import java.util.List;

import org.rifidi.edge.api.service.RifidiAppEsperFactory;
import org.rifidi.edge.api.service.RifidiAppService;
import org.rifidi.edge.notification.TagReadEvent;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

/**
 * Raw tag service -- ReadCycles aren't filtered and are simply passed 
 * 
 * @author Matthew Dean
 */
public class RawTagMonitoringServiceImpl extends RifidiAppService<RawTagSubscriber> implements RawTagMonitoringService {

	/**
	 * 
	 * @param group
	 * @param name
	 */
	public RawTagMonitoringServiceImpl(String group, String name) {
		super(group, name);
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.api.service.tagmonitor.RawTagMonitoringService#subscribe(org.rifidi.edge.api.service.tagmonitor.ReadZoneSubscriber)
	 */
	@Override
	public void subscribe(RawTagSubscriber subscriber) {
		this.subscribe(subscriber, new ArrayList<ReadZone>());
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.api.service.tagmonitor.RawTagMonitoringService#subscribe(org.rifidi.edge.api.service.tagmonitor.ReadZoneSubscriber, org.rifidi.edge.api.service.tagmonitor.ReadZone)
	 */
	@Override
	public void subscribe(RawTagSubscriber subscriber, ReadZone readZone) {
		List<ReadZone> readZones = new ArrayList<ReadZone>();
		readZones.add(readZone);
		this.subscribe(subscriber, readZones);
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.api.service.tagmonitor.RawTagMonitoringService#subscribe(org.rifidi.edge.api.service.tagmonitor.ReadZoneSubscriber, java.util.List)
	 */
	@Override
	public void subscribe(RawTagSubscriber subscriber, List<ReadZone> readZones) {
		RifidiAppEsperFactory esperFactory;
		esperFactory = new RawTagMonitoringEsperFactory(readZones, getCounter());
		this.subscribe(subscriber, esperFactory);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.service.RifidiAppService#createListener(
	 * org.rifidi.edge.api.service.RifidiAppSubscriber)
	 */
	@Override
	protected StatementAwareUpdateListener createListener(final RawTagSubscriber subscriber) {
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

			}
		};
	}

}
