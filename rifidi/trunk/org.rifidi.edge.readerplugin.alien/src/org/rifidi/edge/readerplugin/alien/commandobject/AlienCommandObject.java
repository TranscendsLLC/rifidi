/*
 *  AlienCommandObject.java
 *
 *  Created:	Mar 9, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.alien.commandobject;

import java.io.IOException;

import org.rifidi.edge.readerplugin.alien.Alien9800ReaderSession;

/**
 * This is an Command Object use to formulate individual request/response
 * interactions with an Alien ReaderSession. It provides default behavior for
 * getting and setting properties on a command where the command is of the form
 * 
 * get commandname\n
 * 
 * where the response is of the form
 * 
 * commandname=value\n
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public abstract class AlienCommandObject {

	/** The command to be sent to the Alien ReaderSession */
	protected String command;
	/** The readerSession to send the command to */
	protected Alien9800ReaderSession readerSession;

	/**
	 * Construct a command object.
	 * 
	 * @param command
	 *            A command to be send to the Alien readerSession (see the
	 *            static command strings in AlienReader)
	 * @param readerSession
	 *            A live readerSession to send the command to
	 */
	public AlienCommandObject(String command) {
		this.command = command;
	}

	/**
	 * Set the session on the reader
	 * 
	 * @param session
	 */
	public void setSession(Alien9800ReaderSession session) {
		this.readerSession = session;
	}

	/**
	 * Execute a get or set on the reader
	 * 
	 * @return The return value from the Call
	 * @throws IOException
	 * @throws AlienException
	 */
	public abstract String execute() throws IOException, AlienException;

}
