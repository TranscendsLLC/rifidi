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
 * 
 * @author Matthew Dean matt@transcends.co
 */
public interface RSSIMonitoringService {
	public void subscribe(RSSIReadZoneSubscriber subscriber,
			List<ReadZone> readZones, Float windowTime, TimeUnit timeUnit, Integer countThreshold, Double minAvgRSSIThreshold);
	public void unsubscribe(RSSIReadZoneSubscriber subscriber);
}
