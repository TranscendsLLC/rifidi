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
package org.rifidi.edge.adapter.thingmagic.commandobject;

import java.io.IOException;

import org.rifidi.edge.adapter.thingmagic.ThingmagicReaderSession;


/**
 * This represents a command for the ThingMagic reader.  
 * 
 * @author Matthew Dean
 */
public abstract class ThingmagicCommandObject {
	/** The command to be sent to the Alien SensorSession */
	protected String command;
	/** The sensorSession to send the command to */
	protected ThingmagicReaderSession readerSession;

	/**
	 * Construct a command object.
	 * 
	 * @param command
	 *            A command to be send to the Alien sensorSession (see the
	 *            static command strings in AlienReader)
	 * @param sensorSession
	 *            A live sensorSession to send the command to
	 */
	public ThingmagicCommandObject(String command) {
		this.command = command;
	}

	/**
	 * Set the session on the reader
	 * 
	 * @param session
	 */
	public void setSession(ThingmagicReaderSession session) {
		this.readerSession = session;
	}

	/**
	 * Execute a get or set on the reader
	 * 
	 * @return The return value from the Call
	 * @throws IOException
	 * @throws AlienException
	 */
	public abstract String execute() throws IOException, ThingmagicException;
}
