/**
 * 
 */
package org.rifidi.edge.core.mock;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.rifidi.edge.api.service.tagmonitor.ReadZone;
import org.rifidi.edge.api.service.tagmonitor.ReadZoneMonitoringService;
import org.rifidi.edge.api.service.tagmonitor.ReadZoneSubscriber;
/**
 * @author percent
 */

public class MockReadZoneMonitoringService implements ReadZoneMonitoringService {

	public ReadZoneSubscriber subscriber;
	public List<ReadZone> readZones;
	public Float departureTime;
	public TimeUnit timeUnit;	
	public boolean subscribed;
	
	public MockReadZoneMonitoringService() {
		this.subscribed = false;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.api.service.tagmonitor.ReadZoneMonitoringService#subscribe(org.rifidi.edge.api.service.tagmonitor.ReadZoneSubscriber)
	 */
	@Override
	public void subscribe(ReadZoneSubscriber subscriber) {
		assert false;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.api.service.tagmonitor.ReadZoneMonitoringService#subscribe(org.rifidi.edge.api.service.tagmonitor.ReadZoneSubscriber, java.util.List, java.lang.Float, java.util.concurrent.TimeUnit)
	 */
	@Override
	public void subscribe(ReadZoneSubscriber subscriber,
			List<ReadZone> readZones, Float departureTime, TimeUnit timeUnit) {
		this.subscriber = subscriber;
		this.readZones = readZones;
		this.departureTime = departureTime;
		this.timeUnit = timeUnit;
		subscribed = true;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.api.service.tagmonitor.ReadZoneMonitoringService#subscribe(org.rifidi.edge.api.service.tagmonitor.ReadZoneSubscriber, org.rifidi.edge.api.service.tagmonitor.ReadZone, java.lang.Float, java.util.concurrent.TimeUnit)
	 */
	@Override
	public void subscribe(ReadZoneSubscriber subscriber, ReadZone readZone,
			Float departureTime, TimeUnit timeUnit) {
		assert false;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.api.service.tagmonitor.ReadZoneMonitoringService#unsubscribe(org.rifidi.edge.api.service.tagmonitor.ReadZoneSubscriber)
	 */
	@Override
	public void unsubscribe(ReadZoneSubscriber subscriber) {
		assert subscribed == true && this.subscriber == subscriber;
		assert false;

	}
}
