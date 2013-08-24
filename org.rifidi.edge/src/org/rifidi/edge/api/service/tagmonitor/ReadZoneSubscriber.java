/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
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
