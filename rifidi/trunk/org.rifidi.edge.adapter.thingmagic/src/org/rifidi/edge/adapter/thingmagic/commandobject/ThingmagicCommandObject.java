/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
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
