/**
 * 
 */
package org.rifidi.edge.core.app.api.service.tagmonitor;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This service notifies subscribers every x time units of unique tags in a read
 * zone.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface UniqueTagBatchIntervalService {

	/**
	 * 
	 * @param subscriber
	 * @param readZones
	 * @param notifyInterval
	 * @param timeUnit
	 */
	public void subscribe(UniqueTagBatchIntervalSubscriber subscriber,
			List<ReadZone> readZones, Float notifyInterval, TimeUnit timeUnit);

	/**
	 * Unsubscribe from tag watch notifications
	 * 
	 * @param subscriber
	 * @return
	 */
	public void unsubscribe(UniqueTagBatchIntervalSubscriber subscriber);

}
