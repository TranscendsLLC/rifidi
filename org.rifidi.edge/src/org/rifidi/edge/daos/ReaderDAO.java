/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
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
