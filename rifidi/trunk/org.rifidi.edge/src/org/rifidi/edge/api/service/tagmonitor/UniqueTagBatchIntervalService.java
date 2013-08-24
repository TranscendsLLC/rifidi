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
