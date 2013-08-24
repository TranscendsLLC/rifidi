/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.exceptions;

/**
 * Thrown if a sensorSession is not available.
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public class NoReaderAvailableException extends Exception {

	/** Default serial version. */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.  
	 */
	public NoReaderAvailableException() {
		super();
	}

	/**
	 * Constructor.  
	 * 
	 * @param arg0
	 * @param arg1
	 */
	public NoReaderAvailableException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * Constructor.  
	 * 
	 * @param arg0
	 */
	public NoReaderAvailableException(String arg0) {
		super(arg0);
	}

	/**
	 * Constructor.  
	 * 
	 * @param arg0
	 */
	public NoReaderAvailableException(Throwable arg0) {
		super(arg0);
	}

}
