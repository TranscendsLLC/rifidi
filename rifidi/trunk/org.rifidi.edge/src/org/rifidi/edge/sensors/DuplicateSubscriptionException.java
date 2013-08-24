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
 * Thrown if a given Object is already subscribed to the reader.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class DuplicateSubscriptionException extends Exception {

	/** Default serial version. */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 */
	public DuplicateSubscriptionException() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param arg0
	 */
	public DuplicateSubscriptionException(String arg0) {
		super(arg0);
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 * @param cause
	 */
	public DuplicateSubscriptionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor.
	 * 
	 * @param cause
	 */
	public DuplicateSubscriptionException(Throwable cause) {
		super(cause);
	}

}
