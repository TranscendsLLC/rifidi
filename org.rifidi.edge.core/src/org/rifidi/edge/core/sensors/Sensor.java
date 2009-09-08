/*
 * 
 * Sensor.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
package org.rifidi.edge.core.sensors;

import org.rifidi.edge.core.sensors.exceptions.NotSubscribedException;
import org.rifidi.edge.core.services.notification.data.ReadCycle;


public interface Sensor {

	/**
	 * Get the name of the sensor.
	 * 
	 * @return
	 */
	String getName();

	/**
	 * Check if the sensor is immutable.
	 * 
	 * @return
	 */
	Boolean isImmutable();

	/**
	 * Check if the sensor is in use.
	 * 
	 * @return
	 */
	Boolean isInUse();

	/**
	 * Objects subscribed to the sensor use this method to acquire the read
	 * results.
	 * 
	 * @param reads
	 */
	ReadCycle receive(Object object) throws NotSubscribedException;

	/**
	 * Send sensor results to this sensor.
	 * 
	 * @param cycle
	 */
	public void send(ReadCycle cycle);

}