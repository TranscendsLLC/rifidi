/*
 *  SensorStatusSubscriber.java
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
