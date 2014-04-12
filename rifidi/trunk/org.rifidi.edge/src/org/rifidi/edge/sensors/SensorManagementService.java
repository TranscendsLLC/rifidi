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

import java.util.Collection;
import java.util.Set;


/**
 * Service for managing all modifiable aspects of the sensors.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface SensorManagementService {

	/**
	 * Create a new sensor.
	 * 
	 * @param sensorName
	 * @throws DuplicateSensorNameException
	 */
	void createSensor(String sensorName) throws DuplicateSensorNameException;

	/**
	 * Destroy a sensor.
	 * 
	 * @param sensorName
	 * @throws NoSuchSensorException
	 * @throws ImmutableException
	 * @throws InUseException
	 */
	void destroySensor(String sensorName) throws NoSuchSensorException,
			ImmutableException, InUseException;

	/**
	 * Create a new sensor that is already hooked up to other sensors.
	 * 
	 * @param sensorName
	 * @param childSensors
	 * @throws DuplicateSensorNameException
	 * @throws NoSuchSensorException
	 */
	void createSensor(String sensorName, Collection<String> childSensors)
			throws DuplicateSensorNameException, NoSuchSensorException;

	/**
	 * Change the name of a sensor.
	 * 
	 * @param oldName
	 * @param newName
	 * @throws NoSuchSensorException
	 * @throws ImmutableException
	 * @throws InUseException
	 */
	void renameSensor(String oldName, String newName)
			throws NoSuchSensorException, ImmutableException, InUseException;

	/**
	 * Add a new child to a sensor.
	 * 
	 * @param sensorName
	 * @param childName
	 * @throws ImmutableException
	 * @throws InUseException
	 * @throws NoSuchSensorException
	 */
	void addChild(String sensorName, String childName)
			throws ImmutableException, InUseException, NoSuchSensorException;

	/**
	 * Add several sensors as children to the sensor.
	 * 
	 * @param sensorName
	 * @param childNames
	 * @throws ImmutableException
	 * @throws InUseException
	 * @throws NoSuchSensorException
	 */
	void addChildren(String sensorName, Collection<String> childNames)
			throws ImmutableException, InUseException, NoSuchSensorException;

	/**
	 * Set the sensor's child sensors.
	 * 
	 * @param sensorName
	 * @param childNames
	 * @throws ImmutableException
	 * @throws InUseException
	 * @throws NoSuchSensorException
	 */
	void setChildren(String sensorName, Collection<String> childNames)
			throws ImmutableException, InUseException, NoSuchSensorException;

	/**
	 * Remove one child from a sensor.
	 * 
	 * @param sensorName
	 * @param childName
	 * @throws ImmutableException
	 * @throws InUseException
	 * @throws NoSuchSensorException
	 */
	void removeChild(String sensorName, String childName)
			throws ImmutableException, InUseException, NoSuchSensorException;

	/**
	 * Remove a number of childrens from a sensor.
	 * 
	 * @param sensorName
	 * @param childrenNames
	 * @throws ImmutableException
	 * @throws InUseException
	 * @throws NoSuchSensorException
	 */
	void removeChildren(String sensorName, Collection<String> childrenNames)
			throws ImmutableException, InUseException, NoSuchSensorException;

	/**
	 * Subscribe an object to a sensor.
	 * 
	 * @param subscriber
	 * @param sensorName
	 * @return
	 * @throws NoSuchSensorException
	 * @throws DuplicateSubscriptionException
	 */
	Sensor subscribe(Object subscriber, String sensorName)
			throws NoSuchSensorException, DuplicateSubscriptionException;

	/**
	 * Unsubscribe an object from a sensor.
	 * 
	 * @param subscriber
	 * @param sensorName
	 * @throws NoSuchSensorException
	 * @throws NotSubscribedException
	 */
	void unsubscribe(Object subscriber, String sensorName)
			throws NoSuchSensorException, NotSubscribedException;

	/**
	 * Publish all reads from a sensor to esper.
	 * 
	 * @param sensorName
	 * @throws NoSuchSensorException
	 */
	void publishToEsper(String sensorName) throws NoSuchSensorException;

	/**
	 * Dont publish the reads from a given sensor.
	 * 
	 * @param sensorName
	 * @throws NoSuchSensorException
	 */
	void unpublishFromEsper(String sensorName) throws NoSuchSensorException;

	/**
	 * Get a serializable representation of the given sensor.
	 * 
	 * @param sensorName
	 * @return
	 * @throws NoSuchSensorException
	 */
	SensorDTO getDTO(String sensorName) throws NoSuchSensorException;

	/**
	 * Get the names of all registered sensors.
	 * 
	 * @return
	 */
	Set<String> getSensors();

}
