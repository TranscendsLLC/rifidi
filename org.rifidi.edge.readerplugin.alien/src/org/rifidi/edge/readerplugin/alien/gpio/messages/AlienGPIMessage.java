/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.gpio.messages;

/**
 * @author kyle
 * 
 */
public class AlienGPIMessage extends AlienGPIOMessage {

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.readerplugin.alien.gpio.messages.AlienGPIOMessage#
	 * getEventType()
	 */
	@Override
	public GPIOEvent getEventType() {
		return GPIOEvent.DI;
	}

}
