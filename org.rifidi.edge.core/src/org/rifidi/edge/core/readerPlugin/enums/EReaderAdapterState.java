/*
 *  RifidiConnectionException.java
 *
 *  Created:	Jun 19, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.core.readerPlugin.enums;

/**
 * @author Jerry Maine - jerry@pramari.com
 * The allowable states for a reader
 */
public enum EReaderAdapterState {
	CREATED, CONNECTED, DISCONECTED, DESTROYED, STREAMING, BUSY, ERROR
}
