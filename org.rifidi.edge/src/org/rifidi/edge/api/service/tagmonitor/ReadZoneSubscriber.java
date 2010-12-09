/*
 *  ReadZoneSubscriber.java
 *
 *  Created:	Mar 16, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
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
