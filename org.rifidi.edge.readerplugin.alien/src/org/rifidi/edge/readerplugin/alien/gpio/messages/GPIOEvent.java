/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.gpio.messages;

/**
 * The type of message sent back from the Alien Reader on the IOStream
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public enum GPIOEvent {

	/** A Digital Input event from the Alien GPIO Stream (GPI Event) */
	DI,

	/** A Digital Output event from the Alien GPIO Stream (GPO Event) */
	DO,

	/** A Header message from the ALien GPIO Stream */
	HEADER;

}
