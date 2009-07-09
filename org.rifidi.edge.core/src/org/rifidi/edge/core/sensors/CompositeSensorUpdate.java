package org.rifidi.edge.core.sensors;

import java.util.Collection;

import org.rifidi.edge.core.sensors.exceptions.ImmutableException;
import org.rifidi.edge.core.sensors.exceptions.InUseException;

/**
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface CompositeSensorUpdate extends CompositeSensor {

	/**
	 * Add a new child sensor.
	 * 
	 * @param child
	 * @throws ImmutableException
	 * @throws InUseException
	 */
	void addChild(final SensorUpdate child) throws ImmutableException,
			InUseException;

	/**
	 * Remove a child sensor.
	 * 
	 * @param child
	 * @throws ImmutableException
	 * @throws InUseException
	 */
	void removeChild(final SensorUpdate child) throws ImmutableException,
			InUseException;

	/**
	 * Remove several child sensors.
	 * 
	 * @param children
	 * @throws ImmutableException
	 * @throws InUseException
	 */
	void removeChildren(final Collection<SensorUpdate> children)
			throws ImmutableException, InUseException;
}