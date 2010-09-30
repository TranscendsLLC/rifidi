/**
 * 
 */
package org.rifidi.edge.core.app.api.service.tagmonitor;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This service notifies subscribers of unique tags being seen in a ReadZone. It
 * notifies subscribers as soon as the tag is seen in the readzone and at every
 * x timeunits that the tag is still in the read zone after the first time..
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface UniqueTagIntervalService {

	/**
	 * Subscribe to tag watch notifications. By default, watch every available
	 * reader and antenna and report every 5 seconds for each unique tag.
	 * 
	 * @param subscriber
	 *            The subscriber
	 */
	public void subscribe(UniqueTagIntervalSubscriber subscriber);

	/**
	 * Subscribe to tag watch notifications
	 * 
	 * @param subscriber
	 *            The subscriber
	 * @param readZones
	 *            The readzones to monitor. If the set is empty, monitor all
	 *            readers and antennas.
	 * @param notifyInterval
	 *            The time interval to wait inbetween notification events for
	 *            each tag.
	 * @param timeUnit
	 *            the unit of time used for the notifyIterval
	 */
	public void subscribe(UniqueTagIntervalSubscriber subscriber,
			List<ReadZone> readZones, Float notifyInterval, TimeUnit timeUnit);

	/**
	 * Unsubscribe from tag watch notifications
	 * 
	 * @param subscriber
	 * @return
	 */
	public void unsubscribe(UniqueTagIntervalSubscriber subscriber);

}
