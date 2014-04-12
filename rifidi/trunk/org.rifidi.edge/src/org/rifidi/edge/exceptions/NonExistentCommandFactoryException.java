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
