/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
/**
 * 
 */
package org.rifidi.edge.sensors.sessions;

/**
 * This interface allows sensor plugins to implement the functionality to
 * determine what constitutes a logical message from the sensor. 
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface MessageParsingStrategy {

	/**
	 * This method is called each time a new byte is read. It will return the
	 * full message if a complete message has arrived, otherwise null.
	 * 
	 * @param message
	 * @return the message or null
	 */
	public abstract byte[] isMessage(byte message);

}
