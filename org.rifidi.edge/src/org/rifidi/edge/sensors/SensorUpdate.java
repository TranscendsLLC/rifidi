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
package org.rifidi.edge.sensors;


/**
 * 
 * @author Jochen Mader - jochen@pramari.com
 *
 */
public interface SensorUpdate extends Sensor {

	/**
	 * 
	 * @param name
	 * @throws ImmutableException
	 * @throws InUseException
	 */
	void setName(String name) throws ImmutableException, InUseException;

	/**
	 * Subscribe an object to the sensor.
	 * 
	 * @param object
	 * @throws DuplicateSubscriptionException
	 */
	void subscribe(Object object) throws DuplicateSubscriptionException;

	/**
	 * Unsubscribe an object from the sensor.
	 * 
	 * @param object
	 * @throws NotSubscribedException
	 */
	void unsubscribe(Object object) throws NotSubscribedException;

	/**
	 * Add a sensor to the list of sensors that receive sensor reads from this
	 * sensor.
	 * 
	 * @param receiver
	 */
	void addReceiver(Sensor receiver);

	/**
	 * Remove a sensor from the list of sensors that receive sensor reads from
	 * this sensor.
	 * 
	 * @param receiver
	 */
	void removeReceiver(Sensor receiver);

}
