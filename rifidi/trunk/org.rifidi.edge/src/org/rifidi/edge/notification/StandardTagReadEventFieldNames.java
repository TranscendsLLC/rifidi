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
package org.rifidi.edge.notification;

/**
 * This interface contains several fields that it is common for RFID readers to
 * collect. These names should be used when inserting into the extraInformation
 * hashmap in TagReadEvent.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface StandardTagReadEventFieldNames {

	/** Return Signal Strength Indicator. Expressed as a Float */
	public static final String RSSI = "RSSI";
	/** Speed tag was moving. Expressed as Float */
	public static final String SPEED = "Speed";
	/** Direction tag was moving. Expressed as String */
	public static final String DIRECTION = "Direction";

}
