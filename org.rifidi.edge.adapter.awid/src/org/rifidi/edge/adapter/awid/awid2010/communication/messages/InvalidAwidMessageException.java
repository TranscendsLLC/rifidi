/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.awid.awid2010.communication.messages;

/**
 * Thrown if a byte[] is not a valid Awid Message when parsing
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class InvalidAwidMessageException extends Exception {

	/***/
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public InvalidAwidMessageException() {
		super();
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public InvalidAwidMessageException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public InvalidAwidMessageException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public InvalidAwidMessageException(Throwable arg0) {
		super(arg0);
	}

}
