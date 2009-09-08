/*
 * 
 * MessageProcessingStrategy.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
/**
 * 
 */
package org.rifidi.edge.core.sensors.base.threads;

/**
 * This interface allows sensor plugins to implement the functionality to
 * determine what to do with a logical message once it has been read from the
 * socket
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface MessageProcessingStrategy {

	/**
	 * A hook to allow implementations to do do something with the message when
	 * it has been completely received from the socket
	 * 
	 * @param message
	 *            The complete message
	 */
	abstract void processMessage(byte[] message);

}
