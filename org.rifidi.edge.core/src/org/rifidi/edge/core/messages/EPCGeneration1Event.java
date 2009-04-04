/**
 * 
 */
package org.rifidi.edge.core.messages;

import java.math.BigInteger;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class EPCGeneration1Event extends DatacontainerEvent {

	/** serial version for this class. */
	private static final long serialVersionUID = 1L;

	/** Length of the epc. */
	protected Integer epcLength;

	/**
	 * Constructor.
	 */
	public EPCGeneration1Event() {
		memoryBanks.add(new BigInteger("0"));
	}

	/**
	 * Set the epc memory bank.
	 * 
	 * @param memBank
	 */
	public void setEPCMemory(BigInteger memBank) {
		memoryBanks.set(0, memBank);
	}

	/**
	 * Get the epc memory bank.
	 * 
	 * @return
	 */
	public BigInteger getEPCMemory() {
		return memoryBanks.get(0);
	}

	/**
	 * @return the epcLength
	 */
	public Integer getEpcLength() {
		return epcLength;
	}

	/**
	 * @param epcLength
	 *            the epcLength to set
	 */
	public void setEpcLength(Integer epcLength) {
		this.epcLength = epcLength;
	}

}
