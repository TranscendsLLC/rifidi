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
