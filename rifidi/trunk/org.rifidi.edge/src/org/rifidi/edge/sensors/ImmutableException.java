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

/**
 * Thrown if changing an immutable sensor is attempted.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class ImmutableException extends Exception {

	/** Serial version UID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 * 
	 */
	public ImmutableException() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param arg0
	 * @param arg1
	 */
	public ImmutableException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * Constructor.
	 * 
	 * @param arg0
	 */
	public ImmutableException(String arg0) {
		super(arg0);
	}

	/**
	 * Constructor.
	 * 
	 * @param arg0
	 */
	public ImmutableException(Throwable arg0) {
		super(arg0);
	}

}
