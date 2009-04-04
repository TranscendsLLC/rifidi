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
	
	public String getField(String name) {
		// TODO: tree might be better
		if ("epc".equals(name)) {
			// use hexencoding for equality
			return hex;
		}
		if ("killPwd".equals(name)) {
			return baseEvent.readMemory(0, 0,
					32).toString(16);
		}
		if ("accessPwd".equals(name)) {
			return baseEvent.readMemory(0,
					32, 32).toString(16);
		}
		if ("epcBank".equals(name)) {
			return baseEvent.getEPCMemory().toString(16);
		}
		if ("tidBank".equals(name)) {
			return baseEvent.getTIDMemory().toString(16);
		}
		if ("userBank".equals(name)) {
			return baseEvent.getUserMemory().toString(16);
		}
		if ("afi".equals(name)) {
			return baseEvent.readMemory(1, 8, 24).toString(16);
		}
		if ("nsi".equals(name)) {
			return baseEvent.readMemory(1, 9, 23).toString(16);
		}
		if (name.startsWith("@")) {
			String[] vals = name.substring(1).split(".");
			if (vals.length == 2) {
				return baseEvent.readMemory(Integer.parseInt(vals[0]),
						Integer.parseInt(vals[1]), 0).toString(16);
			}
			return baseEvent.readMemory(Integer.parseInt(vals[0]),
					Integer.parseInt(vals[1]), Integer.parseInt(vals[2]))
					.toString(16);
		}
		throw new RuntimeException("Unknown field name: " + name);
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
