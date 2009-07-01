package org.rifidi.edge.core.sensors.management;

import java.util.Set;

import org.rifidi.edge.core.sensors.LogicalSensor;
import org.rifidi.edge.core.sensors.PhysicalSensor;
import org.rifidi.edge.core.sensors.exceptions.DuplicateSensorNameException;

/**
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface SensorManagementService {

	/**
	 * Factory method for creating a new sensor.
	 * 
	 * @param name
	 */
	LogicalSensor createLogicalSensor(String name)
			throws DuplicateSensorNameException;

	/**
	 * Destroy the sensor with the given name.
	 * 
	 * @param name
	 */
	void destroyLogicalSensor(String name);

	/**
	 * Destroy the given sensor.
	 * 
	 * @param name
	 */
	void destroyLogicalSensor(LogicalSensor name);

	/**
	 * Get a set containing all currently available sensors.
	 */
	Set<PhysicalSensor> getSensors();

	/**
	 * Get a sensor by its name. Returns null if non available.
	 * 
	 * @param name
	 * @return
	 */
	PhysicalSensor getSensor(String name);

}
