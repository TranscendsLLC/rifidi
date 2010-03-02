/*
 * 
 * EPCGeneration2Event.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
package org.rifidi.edge.core.services.notification.data;

import java.math.BigInteger;

import org.apache.commons.codec.binary.Hex;

/**
 * A class that represnets an EPC Class 1 Gen 2 tag
 * 
 * @author Jochen Mader - jochen@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
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
	 * Get the kill password as a hex string. The first character of the return
	 * string is x
	 * 
	 * @return A hex sting with the first character being an x.
	 */
	public String getKillPwd() {
		return "x" + readMemory(0, 0, 32).toString(16);
	}

	/**
	 * Get the kill password as a decimal string.
	 * 
	 * @return
	 */
	public String getKillPwdDecimal() {
		return readMemory(0, 0, 32).toString(10);
	}

	/**
	 * Get the access password as a hex string. The first character of the
	 * return string is x
	 * 
	 * @return a hex string with the first character being a x
	 */
	public String getAccessPwd() {
		return "x" + readMemory(0, 32, 32).toString(16);
	}

	/**
	 * Get the access password as a decimal string
	 * 
	 * @return
	 */
	public String getAccessPwdDecimal() {
		return readMemory(0, 32, 32).toString(10);
	}

	/**
	 * Get the tid memory as a hex string
	 * 
	 * @return
	 */
	public String getTid() {
		return getTIDMemory().toString(16);
	}

	/**
	 * Get the user memory as a hex string
	 * 
	 * @return
	 */
	public String getUser() {
		return getUserMemory().toString(16);
	}

	/**
	 * Get the AFI as a hex String
	 * 
	 * @return
	 */
	public String getAfi() {
		return readMemory(1, 8, 24).toString(16);
	}

	/**
	 * Get the AFI as a decimal string
	 * 
	 * @return
	 */
	public String getAfiDecimal() {
		return readMemory(1, 8, 24).toString(10);
	}

	/**
	 * Get the NSI as a hex String
	 * 
	 * @return
	 */
	public String getNsi() {
		return readMemory(1, 9, 23).toString(16);
	}

	/**
	 * Get the NSI as a decimal string
	 * 
	 * @return
	 */
	public String getNsiDecimal() {
		return readMemory(1, 9, 23).toString(10);
	}

	/**
	 * Set the reserved memory bank.
	 * 
	 * @param memory
	 * @param length
	 *            The number of bits in the reserved memory
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
	 * Get number of bits in the reserved memory.
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
		//hex = memBank.toString(16);
		hex = new String(Hex.encodeHex(memBank.toByteArray()));
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.messages.EPCGeneration1Event#getEPCMemoryLength()
	 */
	@Override
	public Integer getEPCMemoryLength() {
		return memoryBanks.get(1).getLength();
	}

	/**
	 * Set the TID memory bank.
	 * 
	 * @param memory
	 * @param length
	 *            The number of bits in the memory
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
	 * Get the number of bits in the TID memory bank.
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
	 * @param length
	 *            The number of bits in the User Memory
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
	 * Get the number of bits in the user memory.
	 * 
	 * @return
	 */
	public Integer getUserMemoryLength() {
		return memoryBanks.get(3).getLength();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.core.services.notification.data.EPCGeneration1Event#getID()
	 */
	@Override
	public BigInteger getID() {
		return this.memoryBanks.get(1).getMemory();
	}
}
