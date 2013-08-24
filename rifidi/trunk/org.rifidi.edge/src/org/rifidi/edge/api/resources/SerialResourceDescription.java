/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
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
