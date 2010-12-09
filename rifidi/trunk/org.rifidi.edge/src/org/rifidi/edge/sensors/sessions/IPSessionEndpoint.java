/*
 * 
 * PubSubIPSessionSubscriber.java
 *  
 * Created:     Oct 16th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
package org.rifidi.edge.sensors.sessions;

import org.rifidi.edge.sensors.ByteMessage;

/**
 * An interface to implement for objects that wish to be notified when a new
 * message is available from the IPSensorSession
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface IPSessionEndpoint {
	/***
	 * Called when a new message has been received. Remember that the thread of
	 * execution does not belong to this object, so please avoid any calls that
	 * do not return immediately.
	 * 
	 * @param message
	 *            - the new message.
	 */
	void handleMessage(ByteMessage message);
}
