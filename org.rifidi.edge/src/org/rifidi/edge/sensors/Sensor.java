/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.sensors;

import org.rifidi.edge.notification.ReadCycle;
import org.rifidi.edge.services.EsperEventContainer;

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
	EsperEventContainer receive(Object object) throws NotSubscribedException;

	/**
	 * Send sensor results to this sensor.
	 * 
	 * @param cycle
	 */
	public void send(ReadCycle cycle);

	/**
	 * Send a non ReadCycle event to esper.
	 * 
	 * @param event
	 */
	void sendEvent(Object event);
}
