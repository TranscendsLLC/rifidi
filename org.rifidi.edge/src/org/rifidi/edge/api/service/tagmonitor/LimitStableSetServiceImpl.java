/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
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
 * @author Matthew Dean - matt@pramari.com
 * 
 */
public class LimitStableSetServiceImpl extends
		RifidiAppService<LimitStableSetSubscriber> implements
		LimitStableSetService {

	/**
	 * 
	 * @param group
	 * @param name
	 */
	public LimitStableSetServiceImpl(String group, String name) {
		super(group, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.api.service.RifidiAppService#createListener(org.rifidi
	 * .edge.api.service.RifidiAppSubscriber)
	 */
	@Override
	protected StatementAwareUpdateListener createListener(
			final LimitStableSetSubscriber subscriber) {
		return new StatementAwareUpdateListener() {

			@Override
			public void update(EventBean[] arg0, EventBean[] arg1,
					EPStatement arg2, EPServiceProvider arg3) {
				if (arg0 != null) {
					Set<TagReadEvent> stableSet = new HashSet<TagReadEvent>();
					for (EventBean b : arg0) {
						stableSet.add((TagReadEvent) b.getUnderlying());
					}
					subscriber.stableSetReached(stableSet);
				}

			}
		};
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.api.service.tagmonitor.LimitStableSetService#subscribe(org.rifidi.edge.api.service.tagmonitor.LimitStableSetSubscriber, java.util.List, java.lang.Float, java.util.concurrent.TimeUnit, int)
	 */
	@Override
	public void subscribe(LimitStableSetSubscriber subscriber, List<ReadZone> zones,
			Float stableSetTime, TimeUnit stableSetTimeUnit, int limit) {
		this.subscribe(subscriber, zones, stableSetTime, stableSetTimeUnit, limit, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.api.service.tagmonitor.LimitStableSetService#subscribe
	 * (org.rifidi.edge.api.service.tagmonitor.StableSetSubscriber,
	 * java.util.List, java.lang.Float, java.util.concurrent.TimeUnit, int)
	 */
	@Override
	public void subscribe(LimitStableSetSubscriber subscriber, List<ReadZone> zones,
			Float stableSetTime, TimeUnit stableSetTimeUnit, int limit, boolean useRegex) {
		List<ReadZone> readZones = new ArrayList<ReadZone>();
		if (zones != null) {
			readZones.addAll(zones);
		}
		subscribe(subscriber, new LimitStableSetEsperFactory(readZones,
				stableSetTime, stableSetTimeUnit, limit, getCounter(), useRegex));
	}

}
