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
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
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
import org.rifidi.edge.readerplugin.thingmagic.commands.helper.ThingMagicStringBuilder;

/**
 * The command for the thingmagic which will return a list of tags. Currently
 * you can adjust the protocol and the timeout, although more changes may
 * follow.
 * 
 * @author Matthew Dean
 */
public class ThingmagicGetTagListCommand extends AbstractThingmagicCommand {

	/** The readeriD */
	private AtomicReference<String> reader = new AtomicReference<String>();
	/** The logger for this class */
	private Log logger = LogFactory.getLog(ThingmagicGetTagListCommand.class);

	/** The timeout for the tag list command */
	private Integer timeout = 1000;

	/** Will it read class 0 tags? */
	private boolean EPC0 = true;
	/** Will it read class 1 tags? */
	private boolean EPC1 = false;
	/** Will it read Gen2 tags? */
	private boolean GEN2 = false;
	/**  */
	private boolean ISO180006B = false;

	/**
	 * Sets the timeout for this command
	 * 
	 * @param timeout
	 */
	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	/**
	 * Sets whether the reader will look for class 0 tags
	 * 
	 * @param ePC0
	 */
	public void setEPC0(boolean ePC0) {
		EPC0 = ePC0;
	}

	/**
	 * Sets whether the reader will look for class 1 tags
	 * 
	 * @param ePC1
	 */
	public void setEPC1(boolean ePC1) {
		EPC1 = ePC1;
	}

	/**
	 * Sets whether the reader will look for gen2 tags.
	 * 
	 * @param gEN2
	 */
	public void setGEN2(boolean gEN2) {
		GEN2 = gEN2;
	}

	/**
	 * Sets whether the reader will look for ISO-8600-6B tags.
	 * 
	 * @param iSO180006B
	 */
	public void setISO180006B(boolean iSO180006B) {
		ISO180006B = iSO180006B;
	}

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
		// String message = "SELECT id, antenna_id FROM tag_id WHERE "
		// + "protocol_id='EPC1' " + "set time_out=250;\r\n";

		String message = ThingMagicStringBuilder.getCommandString(timeout,
				EPC0, EPC1, GEN2, ISO180006B);

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
			if (logger.isDebugEnabled()) {
				logger.debug("raw msg as string: "
						+ new String(incomingMessage.message));
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
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
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
		String id = splitString[0];
		int numBits = id.length()*4;
		BigInteger epc;
		try {
			epc = new BigInteger(Hex.decodeHex(id.toCharArray()));
		} catch (DecoderException e) {
			throw new RuntimeException("Cannot decode ID: " + id);
		}

		EPCGeneration2Event gen2event = new EPCGeneration2Event();
		gen2event.setEPCMemory(epc, numBits);
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