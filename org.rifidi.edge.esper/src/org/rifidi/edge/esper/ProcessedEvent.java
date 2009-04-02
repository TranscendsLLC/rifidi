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
	private EPCGeneration2Event baseEvent;
	private String pureIdentity;
	private String binary;
	private String hex;
	private int bogus=1;
	/**
	 * @return the bogus
	 */
	public int getBogus() {
		return bogus;
	}

	/**
	 * @return the baseEvent
	 */
	public EPCGeneration2Event getBaseEvent() {
		return baseEvent;
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
	 * @param baseEvent
	 * @param pureIdentity
	 * @param binary
	 * @param hex
	 */
	public ProcessedEvent(EPCGeneration2Event event, String pureIdentity,
			String binary, String hex) {
		super();
		this.baseEvent = event;
		this.pureIdentity = pureIdentity;
		this.binary = binary;
		this.hex = hex;
	}

}
