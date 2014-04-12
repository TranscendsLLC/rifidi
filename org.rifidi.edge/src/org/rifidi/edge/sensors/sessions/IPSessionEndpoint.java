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
