/**
 * 
 */
package org.rifidi.edge.core.app.api.service.tagmonitor;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A StableSet service will return a list of tag to you once a certain time has
 * passed an no new tags have been seen
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface StableSetService {

	/**
	 * Subscribe to the stable set service
	 * 
	 * @param subscriber
	 *            The subscriber
	 * @param zones
	 *            The readzones to monitor
	 * @param stableSetTime
	 *            The time that must pass with no new tags having been seen
	 *            before the stable set will return
	 * @param stableSetTimeUnit
	 * @param unique
	 *            If true, the stable set will only pay attention to unique tags
	 *            and will not count (or return) duplicates
	 */
	public void subscribe(StableSetSubscriber subscriber, List<ReadZone> zones,
			Float stableSetTime, TimeUnit stableSetTimeUnit, boolean unique);

	/**
	 * Unsubscribe from the Stable Set service
	 * 
	 * @param subscriber
	 */
	public void unsubscribe(StableSetSubscriber subscriber);

}
