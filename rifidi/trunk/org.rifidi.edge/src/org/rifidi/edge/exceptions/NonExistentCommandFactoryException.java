/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.exceptions;

/**
 * Thrown if somebody tried to use a command that either doesn't exist or got
 * removed from the registry.
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public class NonExistentCommandFactoryException extends Exception {
	/** Default serial version UID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 */
	public NonExistentCommandFactoryException() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param arg0
	 * @param arg1
	 */
	public NonExistentCommandFactoryException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * Constructor.
	 * 
	 * @param arg0
	 */
	public NonExistentCommandFactoryException(String arg0) {
		super(arg0);
	}

	/**
	 * Constructor.
	 * 
	 * @param arg0
	 */
	public NonExistentCommandFactoryException(Throwable arg0) {
		super(arg0);
	}

}
