/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
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
