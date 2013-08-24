/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.sensors;

import java.util.Collection;


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
