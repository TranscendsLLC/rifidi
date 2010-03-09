package org.rifidi.edge.readerplugin.alien.gpio.messages;

import org.rifidi.edge.readerplugin.alien.commandobject.AlienException;

public abstract class AlienIOListMessageParsingStrategy {

	public abstract AlienGPIOMessage parseMessage(String rawMessage)
			throws AlienException;

}
