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
 * 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class OpticonConstants {
	public static final Integer BAUD = 9600;

	public static final Integer DATA_BITS = SerialPort.DATABITS_8;

	public static final Integer PARITY = SerialPort.PARITY_NONE;

	public static final Integer STOP_BITS = SerialPort.STOPBITS_1;
}
