/*
 *  ReadZoneMonitoringService.java
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

import org.rifidi.edge.core.app.api.util.ReadZoneEsperConstructor;

/**
 * An interfact to help get beginners started with a simple application.
 * 
 * @author Matthew Dean
 */
public interface ReadZoneMonitoringService {
	/**
	 * Subscribe to events coming from this monitoring service.
	 * 
	 * @param rzs
	 *            The class that will recieve the events.
	 * @param readerIDsToSubscribeTo
	 *            A list of readerIDs you wish to subscribe to.
	 * @return True if the subscription was successful, false otherwise.
	 */
	boolean subscribe(ReadZoneEsperConstructor rzec);

	/**
	 * Unsubscribe from this monitoring service.
	 * 
	 * @param rzs
	 * @return
	 */
	public boolean unsubscribe(ReadZoneSubscriber rzs);

}
