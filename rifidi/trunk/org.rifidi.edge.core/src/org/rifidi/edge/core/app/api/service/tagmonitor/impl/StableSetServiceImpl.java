/**
 * 
 */
package org.rifidi.edge.core.app.api.service.tagmonitor.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.rifidi.edge.core.app.api.service.RifidiAppService;
import org.rifidi.edge.core.app.api.service.tagmonitor.ReadZone;
import org.rifidi.edge.core.app.api.service.tagmonitor.StableSetService;
import org.rifidi.edge.core.app.api.service.tagmonitor.StableSetSubscriber;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

/**
 * @author kyle
 *
 */
public class StableSetServiceImpl extends RifidiAppService<StableSetSubscriber> implements
		StableSetService {
	
	/**
	 * @param group
	 * @param name
	 */
	public StableSetServiceImpl(String group, String name) {
		super(group, name);
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.app.api.service.RifidiAppService#createListener(org.rifidi.edge.core.app.api.service.RifidiAppSubscriber)
	 */
	@Override
	protected StatementAwareUpdateListener createListener(
			final StableSetSubscriber subscriber) {
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



	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.app.api.service.tagmonitor.StableSetService#subscribe(org.rifidi.edge.core.app.api.service.tagmonitor.StableSetSubscriber, java.util.List, java.lang.Float, java.util.concurrent.TimeUnit, boolean)
	 */
	@Override
	public void subscribe(StableSetSubscriber subscriber, List<ReadZone> zones,
			Float stableSetTime, TimeUnit stableSetTimeUnit, boolean unique) {
		List<ReadZone> readZones = new ArrayList<ReadZone>();
		if (zones != null) {
			readZones.addAll(zones);
		}
		subscribe(subscriber, new StableSetEsperFactory(readZones,
				stableSetTime, stableSetTimeUnit, unique, getCounter()));

	}

}
