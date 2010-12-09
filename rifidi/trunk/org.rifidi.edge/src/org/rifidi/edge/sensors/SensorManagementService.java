/*
 * 
 * SensorManagementService.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
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