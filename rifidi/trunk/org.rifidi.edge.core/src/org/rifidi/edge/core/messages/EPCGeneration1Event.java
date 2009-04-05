/**
 * 
 */
package org.rifidi.edge.core.messages;

import java.math.BigInteger;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * @author Jochen Mader - jochen@pramari.com
 */
public class EPCGeneration1Event extends DatacontainerEvent {

	/** serial version for this class. */
	private static final long serialVersionUID = 1L;
	/** Pure identity field, generated externally!! */
	private String pureIdentity;
	/** Length of the epc. */
	protected Integer epcLength;
	/** Store a hex copy of the epc for comparison. */
	private String hex = "";

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
		hex = memBank.toString(16);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.messages.DatacontainerEvent#getField(org.rifidi.
	 * edge.core.messages.EpcFields)
	 */
	@Override
	public String getField(EpcFields field) {
		if (EpcFields.EPC.equals(field)) {
			return hex;
		}
		return null;
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

	/**
	 * @return the pureIdentity
	 */
	public String getPureIdentity() {
		return pureIdentity;
	}

	/**
	 * Set the pure identity.
	 */
	public void setPureIdentlty(String pureIdentity) {
		this.pureIdentity = pureIdentity;
	}
}
