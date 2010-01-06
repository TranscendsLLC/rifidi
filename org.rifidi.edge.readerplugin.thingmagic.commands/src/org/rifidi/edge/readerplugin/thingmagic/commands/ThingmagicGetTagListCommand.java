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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	/** The logger for this class */
	private Log logger = LogFactory.getLog(ThingmagicGetTagListCommand.class);

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

		// Output will look like this:
		// Ox303512B1F1FF795511B64E38|1
		String message = "select id,antenna_id from tag_id SET time_out=1000;\r\n";

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
			if(logger.isDebugEnabled()){
				logger.debug("raw msg as string: " + new String(incomingMessage.message));
			}
			String incoming = new String(incomingMessage.message).trim();
			String[] tagString = incoming.split(splitregex);
			for (String i : tagString) {
				i = i.trim();
				if (!i.isEmpty()) {
					// remove the 0x from the tagID
					i = i.substring(2);
					TagReadEvent tre = this.parseThingmagicTagString(i);
					events.add(tre);
				}
			}

			ReadCycle cycle = new ReadCycle(events, reader.get(), System
					.currentTimeMillis());
			sensorSession.getSensor().send(cycle);
			template.send(destination, new ReadCycleMessageCreator(cycle));
		} catch (IOException e) {
			// e.printStackTrace();
		}
	}

	/**
	 * Convert a single ThingMagic tag string into a TagReadEvent. This method
	 * is threadsafe.
	 * 
	 * @param tagDataString
	 *            The tag to convert
	 * @return
	 */
	private TagReadEvent parseThingmagicTagString(String tagDataString) {
		if (logger.isDebugEnabled()) {
			logger.debug("New String to parse: " + tagDataString);
		}
		// the new event
		DatacontainerEvent tagData = null;
		String[] splitString = tagDataString.split("\\|");

		// a big integer representation of the epc
		BigInteger epc = new BigInteger(splitString[0], 16);

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

		Integer antennaID = Integer.parseInt(splitString[1]);

		if (logger.isDebugEnabled()) {
			logger.debug("Tag Read: " + gen2event.getEpc() + " reader: "
					+ this.reader.get() + " ant " + antennaID);
		}

		return new TagReadEvent(this.reader.get(), tagData, antennaID, System
				.currentTimeMillis());
	}

}