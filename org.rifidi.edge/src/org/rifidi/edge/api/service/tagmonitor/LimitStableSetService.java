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
	public void subscribe(LimitStableSetSubscriber subscriber, List<ReadZone> zones,
			Float stableSetTime, TimeUnit stableSetTimeUnit, int limit, boolean useRegex);

	/**
	 * Unsubscribe from the Stable Set service
	 * 
	 * @param subscriber
	 */
	public void unsubscribe(LimitStableSetSubscriber subscriber);
}
