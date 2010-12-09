/*
 * 
 * ReaderDAO.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
package org.rifidi.edge.daos;

import java.util.Set;

import org.rifidi.edge.sensors.AbstractSensor;
import org.rifidi.edge.sensors.AbstractSensorFactory;

/**
 * Interface for Data Access Objects used to access reader objects
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public interface ReaderDAO {
	/**
	 * Get all readers currently available.
	 * 
	 * @return
	 */
	Set<AbstractSensor<?>> getReaders();

	/**
	 * Get a certain reader by its id.
	 * 
	 * @param id
	 * @return
	 */
	AbstractSensor<?> getReaderByID(String id);

	/**
	 * Get all factories currently available.
	 * 
	 * @return
	 */
	Set<AbstractSensorFactory<?>> getReaderFactories();

	/**
	 * Get a factory by its id.
	 * 
	 * @param id
	 * @return
	 */
	AbstractSensorFactory<?> getReaderFactoryByID(String id);
}
