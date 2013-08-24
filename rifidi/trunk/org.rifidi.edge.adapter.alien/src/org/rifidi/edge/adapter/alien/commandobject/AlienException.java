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
package org.rifidi.edge.adapter.alien.commandobject;

/**
 * An Exception that is thrown when reading interacting with an Alien Reader
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class AlienException extends Exception {

	/** SerialVersion FACTORY_ID */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public AlienException() {
	}

	/**
	 * @param arg0
	 */
	public AlienException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public AlienException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public AlienException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
