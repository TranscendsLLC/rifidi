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

	public EPCGeneration2Event() {
		memoryBanks.add(new BigInteger("0"));
		memoryBanks.add(new BigInteger("0"));
		memoryBanks.add(new BigInteger("0"));
		memoryBanks.add(new BigInteger("0"));
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
