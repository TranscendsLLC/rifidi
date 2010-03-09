/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.gpio;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.sensors.sessions.MessageProcessingStrategy;
import org.rifidi.edge.core.services.notification.data.gpio.GPIEvent;
import org.rifidi.edge.readerplugin.alien.Alien9800Reader;
import org.rifidi.edge.readerplugin.alien.commandobject.AlienException;
import org.rifidi.edge.readerplugin.alien.gpio.messages.AlienGPIOMessage;
import org.rifidi.edge.readerplugin.alien.gpio.messages.AlienIOListMessageParsingStrategy;
import org.rifidi.edge.readerplugin.alien.gpio.messages.GPIOEvent;

/**
 * @author kyle
 * 
 */
public class AlienGPIOMessageProcessingStrategy implements
		MessageProcessingStrategy {

	private AlienIOListMessageParsingStrategy IOListMessageParsingStrategy;
	private GPIOEventFactory eventFactory;
	private int currentExternalInput;
	private int currentExternalOutput;
	private Alien9800Reader reader;
	private static final Log logger = LogFactory
			.getLog(AlienGPIOMessageProcessingStrategy.class);

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
			AlienGPIOMessage ioMessage = IOListMessageParsingStrategy
					.parseMessage(new String(message));

			Set<Object> events = eventFactory.getEvent(currentExternalInput,
					currentExternalOutput, ioMessage);
			for (Object event : events) {
				reader.sendEvent(event);
			}

			switch (ioMessage.getEventType()) {
			case DI:
				this.currentExternalInput = ioMessage.getData();
				break;
			case DO:
				this.currentExternalOutput = ioMessage.getData();
				break;
			}

		} catch (AlienException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
