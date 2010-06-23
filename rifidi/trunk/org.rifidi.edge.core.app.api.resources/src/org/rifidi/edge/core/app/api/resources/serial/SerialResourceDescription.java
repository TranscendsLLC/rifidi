/**
 * 
 */
package org.rifidi.edge.core.app.api.resources.serial;

import org.rifidi.edge.core.app.api.resources.ResourceDescription;

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
