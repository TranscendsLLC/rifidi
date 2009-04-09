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
		memoryBanks.add(new MemoryBankLengthTuple(new BigInteger("0"), 1));
		memoryBanks.add(new MemoryBankLengthTuple(new BigInteger("0"), 1));
		memoryBanks.add(new MemoryBankLengthTuple(new BigInteger("0"), 1));
		memoryBanks.add(new MemoryBankLengthTuple(new BigInteger("0"), 1));
	}

	/**
	 * Get the kill password in hex.
	 * 
	 * @return
	 */
	public String getKillPwd() {
		return "x" + readMemory(0, 0, 32).toString(16);
	}

	/**
	 * Get the kill password in binary.
	 * 
	 * @return
	 */
	public String getKillPwdDecimal() {
		return readMemory(0, 0, 32).toString(10);
	}

	/**
	 * Get the access password in hex.
	 * 
	 * @return
	 */
	public String getAccessPwd() {
		return "x" + readMemory(0, 32, 32).toString(16);
	}

	/**
	 * Get the access password in binary.
	 * 
	 * @return
	 */
	public String getAccessPwdDecimal() {
		return readMemory(0, 32, 32).toString(10);
	}

	/**
	 * Get the tid memory in hex.
	 * 
	 * @return
	 */
	public String getTid() {
		return getTIDMemory().toString(16);
	}

	/**
	 * Get the user memory in hex.
	 * 
	 * @return
	 */
	public String getUser() {
		return getUserMemory().toString(16);
	}

	/**
	 * Get the AFI.
	 * 
	 * @return
	 */
	public String getAFI() {
		return readMemory(1, 8, 24).toString(16);
	}

	/**
	 * Get the NSI.
	 * 
	 * @return
	 */
	public String getNSI() {
		return readMemory(1, 9, 23).toString(16);
	}

	/**
	 * Set the reserved memory bank.
	 * 
	 * @param memory
	 * @param length
	 */
	public void setReservedMemory(BigInteger memory, Integer length) {
		memoryBanks.get(0).setMemory(memory);
		memoryBanks.get(0).setLength(length);
	}

	/**
	 * Get the reserved memory bank.
	 */
	public BigInteger getReservedMemory() {
		return memoryBanks.get(0).getMemory();
	}

	/**
	 * Get the length of the reserved memory.
	 * 
	 * @return
	 */
	public Integer getReservedMemoryLength() {
		return memoryBanks.get(0).getLength();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.messages.EPCGeneration1Event#setEPCMemory(java.math
	 * .BigInteger, java.lang.Integer)
	 */
	@Override
	public void setEPCMemory(BigInteger memBank, Integer length) {
		hex = memBank.toString(16);
		memoryBanks.get(1).setMemory(memBank);
		memoryBanks.get(1).setLength(length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.messages.EPCGeneration1Event#getEPCMemory()
	 */
	@Override
	public BigInteger getEPCMemory() {
		return memoryBanks.get(1).getMemory();
	}

	/**
	 * Set the TID memory bank.
	 * 
	 * @param memory
	 * @param length
	 */
	public void setTIDMemory(BigInteger memory, Integer length) {
		memoryBanks.get(2).setMemory(memory);
		memoryBanks.get(2).setLength(length);
	}

	/**
	 * Get the TID memory bank.
	 */
	public BigInteger getTIDMemory() {
		return memoryBanks.get(2).getMemory();
	}

	/**
	 * Get the length of the tid memory.
	 * 
	 * @return
	 */
	public Integer getTIDMemoryLength() {
		return memoryBanks.get(2).getLength();
	}

	/**
	 * Set the user memory bank.
	 * 
	 * @param memory
	 */
	public void setUserMemory(BigInteger memory, Integer length) {
		memoryBanks.get(3).setLength(length);
		memoryBanks.get(3).setMemory(memory);
	}

	/**
	 * Get the user memory bank.
	 */
	public BigInteger getUserMemory() {
		return memoryBanks.get(3).getMemory();
	}

	/**
	 * Get the length of the user memory.
	 * 
	 * @return
	 */
	public Integer getUserMemoryLength() {
		return memoryBanks.get(3).getLength();
	}
}
