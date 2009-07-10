package org.rifidi.edge.core.sensors;

import java.util.Set;

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