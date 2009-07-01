package org.rifidi.edge.core.sensors;

import java.util.Set;

import org.rifidi.edge.core.sensors.impl.LogicalSensorImpl;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

public interface LogicalSensor extends PhysicalSensor {

	/**
	 * Send a newly acquired set of tags to this sensor and all connectedSensors
	 * connected to this one.
	 * 
	 * @param tagReads
	 */
	void send(Set<TagReadEvent> tagReads);

	/**
	 * Add a new sensor to the list of connectedSensors that form this sensor.
	 * 
	 * @param sensor
	 */
	void addSensor(LogicalSensorImpl sensor);

	/**
	 * Remove a sensor from the list of connectedSensors that form this sensor.
	 * 
	 * @param sensor
	 */
	void removeSensor(PhysicalSensor sensor);

	/**
	 * Get a set containing all sensors that form this sensor. The returned set
	 * is a copy of the internal set so changes to it won't propagate.
	 * 
	 * @return the containedSensors
	 */
	Set<LogicalSensorImpl> getContainedSensors();

}