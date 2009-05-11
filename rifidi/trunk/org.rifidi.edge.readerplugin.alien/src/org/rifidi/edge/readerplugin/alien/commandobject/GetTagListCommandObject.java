/*
 *  AlienGetCommandObject.java
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
import java.util.ArrayList;
import java.util.List;

import org.rifidi.edge.core.readers.ByteMessage;
import org.rifidi.edge.readerplugin.alien.Alien9800ReaderSession;

public class GetTagListCommandObject {
	/** The readerSession to send the command to */
	private Alien9800ReaderSession readerSession;

	/**
	 * Construct a command object.
	 * 
	 * @param readerSession
	 *            A live readerSession to send the command to
	 */
	public GetTagListCommandObject(Alien9800ReaderSession readerSession) {
		this.readerSession = readerSession;
	}

	/**
	 * Execute A get
	 * 
	 * @return the value of the property on the alien readerSession
	 * @throws IOException
	 */
	public List<String> executeGet() throws IOException, AlienException {
		String message = Alien9800ReaderSession.PROMPT_SUPPRESS + "get "
				+ Alien9800ReaderSession.COMMAND_TAG_LIST+ Alien9800ReaderSession.NEWLINE;

		readerSession.sendMessage(new ByteMessage(message.getBytes()));

		ByteMessage incomingMessage = readerSession.receiveMessage();

		String incoming = new String(incomingMessage.message).trim();
		ArrayList<String> tags = new ArrayList<String>();
		String[] splitString = incoming.split("\n");
		if(splitString.length==0){
			throw new AlienException("Error while parsing return from Get TagList");
		}
		if(splitString[0].contains("(No Tags)")){
			return tags;
		}else{
			for(int i=0; i<splitString.length; i++){
				tags.add(splitString[i]);
			}
		}
		return tags;
	}

}
