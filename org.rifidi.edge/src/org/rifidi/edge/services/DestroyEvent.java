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
package org.rifidi.edge.services;

/**
 * @author Jochen Mader - jochen@pramari.com
 *
 */
public class DestroyEvent {
	/** Name of the associated ec spec. */
	private String name;

	/**
	 * @param name
	 *            name of the associated ec spec
	 */
	public DestroyEvent(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
