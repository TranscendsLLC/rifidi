/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
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
