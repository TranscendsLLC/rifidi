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
package org.rifidi.edge.core.app.api.service.monitoring;

import java.util.Set;

/**
 * The Read Zone Monitoring Service notifies subscribers when tags have arrived
 * at and departed from a read zone.
 * 
 * By default, departure happens if a tag has not been seen in a read zone for
 * two seconds.
 * 
 * @author Matthew Dean
 * @author Kyle Neumeier - kyle@pramari.com
 */
public interface ReadZoneMonitoringService {

	/**
	 * Subscribe to arrival and departure events from all readers and antennas.
	 * 
	 * @param subscriber
	 *            The subscriber
	 */
	public void subscribe(ReadZoneSubscriber subscriber);

	/**
	 * Subscribe to arrival and departure events from the given read zones
	 * 
	 * @param subscriber
	 *            The subscriber
	 * @param readZones
	 *            The readzones to monitor. If the set is empty, monitor all
	 *            readers and antennas.
	 * @param departureTime
	 *            In seconds. If this amount of time passes since the last time
	 *            a tag has been seen, then fire a departure event.
	 */
	public void subscribe(ReadZoneSubscriber subscriber,
			Set<ReadZone> readZones, Integer departureTime);

	/**
	 * Unsubscribe from arrival and departure events
	 * 
	 * @param subscriber
	 * @return
	 */
	public void unsubscribe(ReadZoneSubscriber subscriber);

}
