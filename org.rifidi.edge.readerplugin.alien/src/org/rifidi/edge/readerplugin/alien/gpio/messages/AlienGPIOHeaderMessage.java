/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.gpio.messages;

/**
 * @author kyle
 * 
 */
public class AlienGPIOHeaderMessage extends AlienGPIOMessage {

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.readerplugin.alien.gpio.messages.AlienGPIOMessage#
	 * getEventType()
	 */
	@Override
	public GPIOEvent getEventType() {
		return GPIOEvent.HEADER;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.readerplugin.alien.gpio.messages.AlienGPIOMessage#getData
	 * ()
	 */
	@Override
	public int getData() {
		throw new RuntimeException("GPIO Header Messages do not have data");
	}

}
