/**
 * 
 */
package org.rifidi.edge.core.messages;

import java.math.BigInteger;

/**
 * @author Jochen Mader - jochen@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class EPCGeneration2Event extends EPCGeneration1Event {

	/** Serial Version ID for this class */
	private static final long serialVersionUID = 1L;

	/** Store a hex copy of the epc for comparison. */
	private String hex = "";

	public EPCGeneration2Event() {
		memoryBanks.add(new BigInteger("0"));
		memoryBanks.add(new BigInteger("0"));
		memoryBanks.add(new BigInteger("0"));
		memoryBanks.add(new BigInteger("0"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.messages.EPCGeneration1Event#getField(org.rifidi
	 * .edge.core.messages.EpcFields)
	 */
	@Override
	public String getField(EpcFields field) {
		// TODO: tree might be better
		if (EpcFields.EPC.equals(field)) {
			// use hexencoding for equality
			return hex;
		}
		if (EpcFields.KILLPWD.equals(field)) {
			return readMemory(0, 0, 32).toString(16);
		}
		if (EpcFields.ACCESSPWD.equals(field)) {
			return readMemory(0, 32, 32).toString(16);
		}
		if (EpcFields.EPCBANK.equals(field)) {
			return getEPCMemory().toString(16);
		}
		if (EpcFields.TIDBANK.equals(field)) {
			return getTIDMemory().toString(16);
		}
		if (EpcFields.USERBANK.equals(field)) {
			return getUserMemory().toString(16);
		}
		if (EpcFields.AFI.equals(field)) {
			return readMemory(1, 8, 24).toString(16);
		}
		if (EpcFields.NSI.equals(field)) {
			return readMemory(1, 9, 23).toString(16);
		}
		return null;
	}

	/**
	 * Set the reserved memory bank.
	 * 
	 * @param memory
	 */
	public void setReservedMemory(BigInteger memory) {
		memoryBanks.set(0, memory);
	}

	/**
	 * Get the reserved memory bank.
	 */
	public BigInteger getReservedMemory() {
		return memoryBanks.get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.messages.EPCGeneration1Event#setEPCMemory(java.math
	 * .BigInteger)
	 */
	@Override
	public void setEPCMemory(BigInteger memBank) {
		hex = memBank.toString(16);
		memoryBanks.set(1, memBank);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.messages.EPCGeneration1Event#getEPCMemory()
	 */
	@Override
	public BigInteger getEPCMemory() {
		return memoryBanks.get(1);
	}

	/**
	 * Set the TID memory bank.
	 * 
	 * @param memory
	 */
	public void setTIDMemory(BigInteger memory) {
		memoryBanks.set(2, memory);
	}

	/**
	 * Get the TID memory bank.
	 */
	public BigInteger getTIDMemory() {
		return memoryBanks.get(2);
	}

	/**
	 * Set the user memory bank.
	 * 
	 * @param memory
	 */
	public void setUserMemory(BigInteger memory) {
		memoryBanks.set(3, memory);
	}

	/**
	 * Get the user memory bank.
	 */
	public BigInteger getUserMemory() {
		return memoryBanks.get(3);
	}
}
