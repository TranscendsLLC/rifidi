/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.api.service.sensormonitor;

import java.util.Set;

/**
 * The SensorStatusMonitoringService notifies subscribers of changes to Sensor
 * states, such as when a sensor is connected or disconnected
 * 
 * @author Matthew Dean
 * @author Kyle Neumeier - kyle@pramari.com
 */
public interface SensorStatusMonitoringService {

	/**
	 * Subscribe to sensor status events.
	 * 
	 * @param subscriber
	 *            The subscriber to notify
	 * @param sensorsToMonitor
	 *            A list of Sensor IDs to monitor. If empty, monitor all
	 *            sensors.
	 */
	void subscribe(SensorStatusSubscriber subscriber,
			Set<String> sensorsToMonitor);

	/**
	 * Unsubscribe from SensorStatus events
	 * 
	 * @param subscriber
	 *            the Subscriber to remove
	 */
	void unsubscribe(SensorStatusSubscriber subscriber);
}
