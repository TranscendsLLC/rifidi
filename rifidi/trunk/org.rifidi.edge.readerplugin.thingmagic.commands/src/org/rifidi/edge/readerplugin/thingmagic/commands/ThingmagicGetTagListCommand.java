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
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.rifidi.edge.core.sensors.messages.ByteMessage;
import org.rifidi.edge.core.services.notification.data.DatacontainerEvent;
import org.rifidi.edge.core.services.notification.data.EPCGeneration2Event;
import org.rifidi.edge.core.services.notification.data.ReadCycle;
import org.rifidi.edge.core.services.notification.data.ReadCycleMessageCreator;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;
import org.rifidi.edge.readerplugin.thingmagic.AbstractThingmagicCommand;
import org.rifidi.edge.readerplugin.thingmagic.ThingmagicReaderSession;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class ThingmagicGetTagListCommand extends AbstractThingmagicCommand {

	/** The readeriD */
	private AtomicReference<String> reader = new AtomicReference<String>();

	/**
	 * 
	 * @param commandID
	 */
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
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		String splitregex = "" + new Character((char) 0x0a);

		String message = "select id from tag_id;\r\n";

		try {
			((ThingmagicReaderSession) this.sensorSession)
					.sendMessage(new ByteMessage(message.getBytes()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		ByteMessage incomingMessage;

		Set<TagReadEvent> events = new HashSet<TagReadEvent>();

		try {
			incomingMessage = ((ThingmagicReaderSession) this.sensorSession)
					.receiveMessage();
			String incoming = new String(incomingMessage.message).trim();
			String[] splitstring = incoming.split(splitregex);
			for (String i : splitstring) {
				i = i.trim();
				if (!i.isEmpty()) {
					i = i.substring(2);
					TagReadEvent tre = this.getTagReadEvent(i);
					events.add(tre);
				}
			}
			ReadCycle cycle = new ReadCycle(events, reader.get(), System
					.currentTimeMillis());
			sensorSession.getSensor().send(cycle);
			template.send(destination, new ReadCycleMessageCreator(cycle));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Convert an AlienTag into a TagReadEvent. This method is threadsafe.
	 * 
	 * @param alienTag
	 *            The tag to convert
	 * @return
	 */
	private TagReadEvent getTagReadEvent(String tagDataString) {
		// the new event
		DatacontainerEvent tagData = null;
		// a big integer representation of the epc
		BigInteger epc = new BigInteger(tagDataString, 16);

		EPCGeneration2Event gen2event = new EPCGeneration2Event();
		// make some wild guesses on the length of the epc field
		if (epc.bitLength() > 96) {
			gen2event.setEPCMemory(epc, 192);
		} else if (epc.bitLength() > 64) {
			gen2event.setEPCMemory(epc, 96);
		} else {
			gen2event.setEPCMemory(epc, 64);
		}
		tagData = gen2event;

		return new TagReadEvent(this.reader.get(), tagData, 0, System
				.currentTimeMillis());
	}

}
