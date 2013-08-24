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
