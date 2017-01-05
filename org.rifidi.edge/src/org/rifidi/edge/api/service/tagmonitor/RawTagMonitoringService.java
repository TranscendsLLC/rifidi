/*******************************************************************************
 * Copyright (c) 2015 Transcends, LLC.
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

/**
 * This is an implementation of 
 * 
 * @author Matthew Dean
 */
public interface RawTagMonitoringService {
	/**
	 * Subscribe to tag feeds from all readers and antennas.
	 * 
	 * @param subscriber
	 *            The subscriber
	 */
	public void subscribe(RawTagSubscriber subscriber);

	/**
	 * Subscribe to tag feeds from the given read zones
	 * 
	 * @param subscriber
	 *            The subscriber
	 * @param readZones
	 *            The readzones to monitor. If the set is empty, monitor all
	 *            readers and antennas.
	 * @param departureTime
	 *            If this amount of time passes since the last time a tag has
	 *            been seen, then fire a departure event.
	 * @param timeUnit
	 *            The unit used for the departure time.
	 */
	public void subscribe(RawTagSubscriber subscriber, List<ReadZone> readZones);

	/**
	 * Subscribe to the raw tag feed of the given read zone.
	 * 
	 * @param subscriber
	 * @param readZone
	 * @param departureTime
	 * @param timeUnit
	 */
	public void subscribe(RawTagSubscriber subscriber, ReadZone readZone);

	/**
	 * Unsubscribe from arrival and departure events
	 * 
	 * @param subscriber
	 * @return
	 */
	public void unsubscribe(RawTagSubscriber subscriber);
}
