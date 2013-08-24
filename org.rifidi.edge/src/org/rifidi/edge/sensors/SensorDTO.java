/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
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
