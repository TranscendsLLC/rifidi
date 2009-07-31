/**
 * 
 */
package org.rifidi.edge.demo1.api;

import java.io.Serializable;

/**
 * Message sent when a tag has arrived in the building.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class HasArrivedMessage implements Serializable {

	/** Serial version. */
	private static final long serialVersionUID = 1L;
	/** EPC id of the tag. */
	private final String epc;
	/** ID of the gate the epc arrived hat. */
	private final Integer gateId;

	/**
	 * Constructor.
	 * 
	 * @param epc
	 *            the epc id of the tag.
	 * @param gateID
	 *            ID of the gate
	 */
	public HasArrivedMessage(final String epc, final Integer gateID) {
		this.epc = epc;
		this.gateId = gateID;
	}

	/**
	 * @return the epc
	 */
	public String getEpc() {
		return epc;
	}

	/**
	 * @return the gateId
	 */
	public Integer getGateId() {
		return gateId;
	}

}
