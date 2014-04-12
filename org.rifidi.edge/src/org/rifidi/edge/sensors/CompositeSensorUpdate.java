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
