/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
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
