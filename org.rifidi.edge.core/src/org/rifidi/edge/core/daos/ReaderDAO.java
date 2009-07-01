package org.rifidi.edge.core.daos;

import java.util.Set;

import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.base.AbstractSensorFactory;

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
