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
