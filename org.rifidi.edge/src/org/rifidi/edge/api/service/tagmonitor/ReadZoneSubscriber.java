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

import org.rifidi.edge.api.service.RifidiAppSubscriber;
import org.rifidi.edge.notification.TagReadEvent;

/**
 * Implement this class to subscribe to get updates when subscribed to a
 * ReadZoneMonitoringService.
 * 
 * @author Matthew Dean
 */
public interface ReadZoneSubscriber extends RifidiAppSubscriber {
	/**
	 * The method that will be called when an arrival event has happened.
	 * 
	 * @param event
	 *            The Tag that arrived
	 */
	public void tagArrived(TagReadEvent tag);

	/**
	 * The method that will be called when a departed event has happened.
	 * 
	 * @param event
	 *            The tag that departed.
	 */
	public void tagDeparted(TagReadEvent tag);
}
