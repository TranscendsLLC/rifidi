/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.opticon;

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
