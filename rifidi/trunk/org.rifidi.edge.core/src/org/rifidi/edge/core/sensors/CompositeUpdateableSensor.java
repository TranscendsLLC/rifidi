package org.rifidi.edge.core.sensors;

import java.util.Collection;
import java.util.Set;

import org.rifidi.edge.core.sensors.exceptions.ImmutableException;
import org.rifidi.edge.core.sensors.exceptions.InUseException;

/**
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface CompositeUpdateableSensor extends UpdateableSensor {

	/**
	 * Add a new child sensor.
	 * 
	 * @param child
	 * @throws ImmutableException
	 * @throws InUseException
	 */
	void addChild(final UpdateableSensor child) throws ImmutableException,
			InUseException;

	/**
	 * Remove a child sensor.
	 * 
	 * @param child
	 * @throws ImmutableException
	 * @throws InUseException
	 */
	void removeChild(final UpdateableSensor child) throws ImmutableException,
			InUseException;

	/**
	 * Remove several child sensors.
	 * 
	 * @param children
	 * @throws ImmutableException
	 * @throws InUseException
	 */
	void removeChildren(final Collection<UpdateableSensor> children)
			throws ImmutableException, InUseException;

	/**
	 * Get the names of child sensors this sensor has.
	 * 
	 * @return
	 */
	Set<String> getChildren();
}