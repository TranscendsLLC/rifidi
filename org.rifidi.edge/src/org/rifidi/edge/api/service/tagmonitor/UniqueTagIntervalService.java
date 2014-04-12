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

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This service notifies subscribers of unique tags being seen in a ReadZone. It
 * notifies subscribers as soon as the tag is seen in the readzone and at every
 * x timeunits that the tag is still in the read zone after the first time..
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface UniqueTagIntervalService {

	/**
	 * Subscribe to tag watch notifications. By default, watch every available
	 * reader and antenna and report every 5 seconds for each unique tag.
	 * 
	 * @param subscriber
	 *            The subscriber
	 */
	public void subscribe(UniqueTagIntervalSubscriber subscriber);

	/**
	 * Subscribe to tag watch notifications
	 * 
	 * @param subscriber
	 *            The subscriber
	 * @param readZones
	 *            The readzones to monitor. If the set is empty, monitor all
	 *            readers and antennas.
	 * @param notifyInterval
	 *            The time interval to wait inbetween notification events for
	 *            each tag.
	 * @param timeUnit
	 *            the unit of time used for the notifyIterval
	 */
	public void subscribe(UniqueTagIntervalSubscriber subscriber,
			List<ReadZone> readZones, Float notifyInterval, TimeUnit timeUnit);

	/**
	 * Unsubscribe from tag watch notifications
	 * 
	 * @param subscriber
	 * @return
	 */
	public void unsubscribe(UniqueTagIntervalSubscriber subscriber);

}
