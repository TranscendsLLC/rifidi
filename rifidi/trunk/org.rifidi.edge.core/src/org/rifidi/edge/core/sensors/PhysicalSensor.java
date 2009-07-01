package org.rifidi.edge.core.sensors;

import java.util.Set;

import org.rifidi.edge.core.sensors.exceptions.DuplicateSubscriptionException;
import org.rifidi.edge.core.sensors.exceptions.NotSubscribedException;
import org.rifidi.edge.core.sensors.impl.LogicalSensorImpl;

public interface PhysicalSensor {

	/**
	 * Get all tag reads since the last receive call.
	 * 
	 * @param receiver
	 * @return
	 */
	Set<TagRead> receive(Object receiver);

	/**
	 * Subscribe an object as a receiver for tag reads.
	 * 
	 * @param receiver
	 * @throws DuplicateSubscriptionException
	 */
	void subscribe(Object receiver) throws DuplicateSubscriptionException;

	/**
	 * Unsubscribe an object as a receiver.
	 * 
	 * @param receiver
	 * @throws NotSubscribedException
	 */
	void unsubscribe(Object receiver) throws NotSubscribedException;

	/**
	 * Connect a sensor to this sensor. The connected sensor will receive all
	 * reads this sensor receives.
	 * 
	 * @param sensor
	 */
	void connectSensor(LogicalSensorImpl sensor);

	/**
	 * Disconnect a sensor from this sensor.
	 * 
	 * @param sensor
	 */
	void disconnectSensor(PhysicalSensor sensor);

	/**
	 * Get a set containing all sensors that are connected to this sensor. The
	 * returned set is a copy of the internal set so changes to it won't
	 * propagate.
	 * 
	 * @return the connectedSensors
	 */
	Set<LogicalSensorImpl> getConnectedSensors();

}