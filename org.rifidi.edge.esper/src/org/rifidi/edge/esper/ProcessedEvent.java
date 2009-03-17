/**
 * 
 */
package org.rifidi.edge.esper;

import org.rifidi.edge.core.messages.EPCGeneration2Event;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class ProcessedEvent {
	private EPCGeneration2Event event;
	private String pureIdentity;
	private String binary;
	private String hex;

	/**
	 * @return the event
	 */
	public EPCGeneration2Event getEvent() {
		return event;
	}

	/**
	 * @return the pureIdentity
	 */
	public String getPureIdentity() {
		return pureIdentity;
	}

	/**
	 * @return the binary
	 */
	public String getBinary() {
		return binary;
	}

	/**
	 * @return the hex
	 */
	public String getHex() {
		return hex;
	}

	/**
	 * @param event
	 * @param pureIdentity
	 * @param binary
	 * @param hex
	 */
	public ProcessedEvent(EPCGeneration2Event event, String pureIdentity,
			String binary, String hex) {
		super();
		this.event = event;
		this.pureIdentity = pureIdentity;
		this.binary = binary;
		this.hex = hex;
	}

}
