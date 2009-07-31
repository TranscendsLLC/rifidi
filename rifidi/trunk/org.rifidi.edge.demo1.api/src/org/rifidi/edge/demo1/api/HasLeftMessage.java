/**
 * 
 */
package org.rifidi.edge.demo1.api;

import java.io.Serializable;

/**
 * Message sent when a certain tag has left the building.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class HasLeftMessage implements Serializable {

	/** Serial version. */
	private static final long serialVersionUID = 1L;
	/** EPC id of the tag. */
	private final String epc;
	/** ID of the gate the epc left through. */
	private final Integer gateId;

	/**
	 * Constructor.
	 * 
	 * @param epc
	 *            the epc ID of the tag.
	 * @param gateID
	 *            ID of the gate
	 */
	public HasLeftMessage(final String epc, final Integer gateID) {
		this.epc = epc;
		this.gateId = gateID;
	}

	/**
	 * Get the EPC number of the tag.
	 * 
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
