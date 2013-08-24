/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.services;

/**
 * Exception to use if there was a problem when provisioning.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class CannotProvisionException extends Exception {

	/***/
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public CannotProvisionException() {
		super();
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public CannotProvisionException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public CannotProvisionException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public CannotProvisionException(Throwable arg0) {
		super(arg0);
	}

}
