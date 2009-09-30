/*
 *  ThingmagicGetTagListCommand.java
 *
 *  Created:	Sep 24, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.thingmagic.commands;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import org.rifidi.edge.core.sensors.messages.ByteMessage;
import org.rifidi.edge.readerplugin.thingmagic.AbstractThingmagicCommand;
import org.rifidi.edge.readerplugin.thingmagic.ThingmagicReaderSession;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class ThingmagicGetTagListCommand extends AbstractThingmagicCommand {

	private ThingmagicReaderSession readerSession;
	
	/** The readeriD */
	private AtomicReference<String> reader = new AtomicReference<String>();
	
	public ThingmagicGetTagListCommand(String commandID) {
		super(commandID);
	}
	
	/**
	 * Set the name of the reader this command is associated with.
	 * 
	 * @param reader
	 */
	public void setReader(String reader) {
		this.reader.set(reader);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		String message = "select id, protocol_id from tag_id;";

		try {
			this.readerSession.sendMessage(new ByteMessage(message.getBytes()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ByteMessage incomingMessage;
		try {
			incomingMessage = this.readerSession.receiveMessage();
			String incoming = new String(incomingMessage.message).trim();
			
			System.out.println("RECIEVED MESSAGE: " + incoming);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
