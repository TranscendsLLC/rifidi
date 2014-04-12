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
package org.rifidi.edge.api.resources;


/**
 * This class is the description of a Serial Resource
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class SerialResourceDescription extends ResourceDescription implements
		SerialProperties {

	/**
	 * Get the port to connect to
	 * 
	 * @return
	 */
	String getPort() {
		return getProperty(PORT);
	}

	/**
	 * Get the Baud
	 * 
	 * @return
	 */
	int getBaud() {
		return Integer.parseInt(getProperty(BAUD));
	}

	/**
	 * Get the databits
	 * 
	 * @return
	 */
	int getDatabits() {
		return Integer.parseInt(getProperty(DATABITS));
	}

	/**
	 * Get the parity
	 * 
	 * @return
	 */
	int getParity() {
		return Integer.parseInt(getProperty(PARITY));
	}

	/**
	 * Get the stopbits
	 * 
	 * @return
	 */
	int getStopbits() {
		return Integer.parseInt(getProperty(STOPBITS));
	}

}
