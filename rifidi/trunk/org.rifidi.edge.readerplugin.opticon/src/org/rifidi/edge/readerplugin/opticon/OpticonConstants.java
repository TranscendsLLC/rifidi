/*
 *  OpticonConstants.java
 *
 *  Created:	May 12, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.opticon;

import gnu.io.SerialPort;

/**
 * The set of constants for the barcode reader. This may need to be set to
 * outside parameters.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class OpticonConstants {
	/**
	 * The Baud that we will be communicating with.  
	 */
	public static final Integer BAUD = 9600;
	
	/**
	 * The Data Bits required.
	 */
	public static final Integer DATA_BITS = SerialPort.DATABITS_8;

	/**
	 * The Parity required.
	 */
	public static final Integer PARITY = SerialPort.PARITY_NONE;

	/**
	 * The Stop Bits required.  
	 */
	public static final Integer STOP_BITS = SerialPort.STOPBITS_1;
}
