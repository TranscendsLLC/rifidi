package org.rifidi.edge.core.sensors;

import java.util.Set;

public interface Sensor {

	/**
	 * Get the name of the sensor.
	 * 
	 * @return
	 */
	String getName();

	/**
	 * Get the names of sensors that are children of this sensor.
	 * 
	 * @return
	 */
	Set<String> getChildren();

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

}