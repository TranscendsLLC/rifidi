/*
 *  ThingmagicCommandObject.java
 *
 *  Created:	Sep 23, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.thingmagic.commandobject;

import java.io.IOException;

import org.rifidi.edge.readerplugin.thingmagic.ThingmagicReaderSession;


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
