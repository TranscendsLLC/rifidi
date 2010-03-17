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
package org.rifidi.edge.core.app.api;

import org.rifidi.edge.core.app.api.events.RFIDEvent;

/**
 * Implement this class to subscribe to get updates when subscribed to a
 * ReadZoneMonitoringService.  
 * 
 * @author Matthew Dean
 */
public interface ReadZoneSubscriber {
	/**
	 * The method that will be called when an event has happened. 
	 * 
	 * @param eventList
	 * @param added
	 */
	public void handleEvent(RFIDEvent event);
}
