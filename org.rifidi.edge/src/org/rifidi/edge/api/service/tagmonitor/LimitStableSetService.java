/**
 * 
 */
package org.rifidi.edge.api.service.tagmonitor;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public interface LimitStableSetService {
	/**
	 * Subscribe to the stable set service. Unlike the StableSetService, the
	 * LimitStableSetService can terminate if either the sliding window times
	 * out or the given limit of tags is reached. Uniqueness is assumed for this
	 * StableSet.
	 * 
	 * @param subscriber
	 *            The subscriber
	 * @param zones
	 *            The readzones to monitor
	 * @param stableSetTime
	 *            The time that must pass with no new tags having been seen
	 *            before the stable set will return
	 * @param stableSetTimeUnit
	 * @param limit
	 *            The limit of tags that are being looked for. If this limit is
	 *            hit before the stableSetTime is reached, it will return all
	 *            seen tags.
	 */
	public void subscribe(LimitStableSetSubscriber subscriber, List<ReadZone> zones,
			Float stableSetTime, TimeUnit stableSetTimeUnit, int limit);

	/**
	 * Unsubscribe from the Stable Set service
	 * 
	 * @param subscriber
	 */
	public void unsubscribe(LimitStableSetSubscriber subscriber);
}
