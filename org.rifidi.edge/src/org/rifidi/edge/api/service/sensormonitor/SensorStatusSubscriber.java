/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.api.service.sensormonitor;

import org.rifidi.edge.api.service.RifidiAppSubscriber;
import org.rifidi.edge.notification.SensorStatusEvent;

/**
 * Implement this class to subscribe to get updates when subscribed to a
 * SensorMonitoringService.
 * 
 * @author Matthew Dean
 * 
 */
public interface SensorStatusSubscriber extends RifidiAppSubscriber {
	/**
	 * Whenever a sensor status event occurs, this method will be called.
	 * 
	 * @param event
	 *            The sensor status event.
	 */
	public void handleSensorStatusEvent(SensorStatusEvent event);
}
