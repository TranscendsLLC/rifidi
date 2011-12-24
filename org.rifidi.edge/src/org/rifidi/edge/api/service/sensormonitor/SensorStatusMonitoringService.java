/*
 *  SensorStatusMonitoringService.java
 *
 *  Created:	Mar 23, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
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
