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
package org.rifidi.edge.adapter.alien.gpio;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.adapter.alien.Alien9800Reader;
import org.rifidi.edge.adapter.alien.commandobject.AlienException;
import org.rifidi.edge.adapter.alien.gpio.messages.AlienGPIOMessage;
import org.rifidi.edge.adapter.alien.gpio.messages.AlienIOListMessageParsingStrategy;
import org.rifidi.edge.sensors.sessions.MessageProcessingStrategy;

/**
 * This is an implementation of the MessageProcessingStrategy for Alien's
 * IOStream. The processMessage is called whenever an IO Message is received.
 * 
 * This method converts IOMessages from the Alien reader into GPIEvents or
 * GPOEvents and puts them into esper.
 * 
 * It's important to note that this class relys on the current state of the
 * external input and output, so a new instance should not be created for every
 * message that comes in.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AlienGPIOMessageProcessingStrategy implements
		MessageProcessingStrategy {

	/** This object converts an raw string to a AlienGPIOMessage */
	private AlienIOListMessageParsingStrategy IOListMessageParsingStrategy;
	/** This object converts an AlienGPIOMessage to a GPIEvent or GPOEvent */
	private GPIOEventFactory eventFactory;
	/** The current state of the ExternalInput */
	private int currentExternalInput;
	/** The current state of the ExternalOutput */
	private int currentExternalOutput;
	/** The Alien Reader used to submit events to esper */
	private Alien9800Reader reader;
	/** The logger for this class */
	private static final Log logger = LogFactory
			.getLog(AlienGPIOMessageProcessingStrategy.class);

	/**
	 * Constructor
	 * 
	 * @param iOListMessageParsingStrategy
	 *            The strategy use to parse strings into AlienGPIOMessage
	 *            objects
	 * @param reader
	 *            The reader used to submit events to esper
	 * @param currentExternalInput
	 *            The initial state of the externalinputs
	 * @param currentExternalOutput
	 *            the initial state of the externaloutputs
	 */
	public AlienGPIOMessageProcessingStrategy(
			AlienIOListMessageParsingStrategy iOListMessageParsingStrategy,
			Alien9800Reader reader, int currentExternalInput,
			int currentExternalOutput) {
		super();
		this.reader = reader;
		IOListMessageParsingStrategy = iOListMessageParsingStrategy;
		eventFactory = new GPIOEventFactory(reader.getID());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.sensors.sessions.MessageProcessingStrategy#
	 * processMessage(byte[])
	 */
	@Override
	public void processMessage(byte[] message) {
		try {

			// first parse the string into an AlienGPIOMessage
			AlienGPIOMessage ioMessage = IOListMessageParsingStrategy
					.parseMessage(new String(message));

			// Submit the event(s) to esper
			Set<Object> events = eventFactory.getEvent(currentExternalInput,
					currentExternalOutput, ioMessage);
			for (Object event : events) {
				reader.sendEvent(event);
			}

			// update the state of the external input or output
			switch (ioMessage.getEventType()) {
			case DI:
				this.currentExternalInput = ioMessage.getData();
				break;
			case DO:
				this.currentExternalOutput = ioMessage.getData();
				break;
			}

		} catch (AlienException e) {
			logger.warn("Exception while " + "processing message: "
					+ e.getMessage());
		}
	}

}
