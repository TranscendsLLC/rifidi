/*
 *  ThingmagicGetTagListCommandObject.java
 *
 *  Created:	Sep 24, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.thingmagic.commandobject;

import java.io.IOException;
import java.util.List;

import org.rifidi.edge.core.sensors.messages.ByteMessage;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;
import org.rifidi.edge.readerplugin.thingmagic.ThingmagicReaderSession;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class ThingmagicGetTagListCommandObject {

	private ThingmagicReaderSession readerSession;

	/**
	 * Construct a command object.
	 * 
	 * @param sensorSession
	 *            A live sensorSession to send the command to
	 */
	public ThingmagicGetTagListCommandObject(
			ThingmagicReaderSession readerSession) {
		this.readerSession = readerSession;
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public List<TagReadEvent> execute() throws IOException {

		String message = "select id, protocol_id from tag_id;";

		this.readerSession.sendMessage(new ByteMessage(message.getBytes()));
		
		ByteMessage incomingMessage = this.readerSession.receiveMessage();
		String incoming = new String(incomingMessage.message).trim();
		
		System.out.println("RECIEVED MESSAGE: " + incoming);

		return null;
	}

}
