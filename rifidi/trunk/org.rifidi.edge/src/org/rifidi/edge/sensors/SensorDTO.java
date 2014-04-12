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
/**
 * 
 */
package org.rifidi.edge.sensors;

import java.util.Set;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class SensorDTO {
	private final Set<String> children;
	private final String name;
	private final boolean composite;

	/**
	 * Constructor.
	 * 
	 * @param name
	 * @param children
	 * @param composite
	 */
	public SensorDTO(final String name, final Set<String> children,
			final boolean composite) {
		this.name = name;
		this.children = children;
		this.composite = composite;
	}

	/**
	 * @return the children
	 */
	public Set<String> getChildren() {
		return children;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the composite
	 */
	public boolean isComposite() {
		return composite;
	}

}
