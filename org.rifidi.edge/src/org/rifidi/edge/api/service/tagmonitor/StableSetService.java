/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.api.service.tagmonitor;

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
