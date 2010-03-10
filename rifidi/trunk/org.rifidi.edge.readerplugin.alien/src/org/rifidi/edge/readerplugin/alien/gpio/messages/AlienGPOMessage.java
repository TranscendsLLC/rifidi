package org.rifidi.edge.readerplugin.alien.gpio.messages;

/**
 * This class represents an Output IO message from an Alien reader
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AlienGPOMessage extends AlienGPIOMessage {

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.readerplugin.alien.gpio.messages.AlienGPIOMessage#
	 * getEventType()
	 */
	@Override
	public GPIOEvent getEventType() {
		return GPIOEvent.DO;
	}

}
