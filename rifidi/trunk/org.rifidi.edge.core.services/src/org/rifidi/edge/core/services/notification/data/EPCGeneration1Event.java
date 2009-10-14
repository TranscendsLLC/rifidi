/*
 * 
 * EPCGeneration1Event.java
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

/**
 * A class that represents a EPC Class 1 Gen 1 Tag
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * @author Jochen Mader - jochen@pramari.com
 */
public class EPCGeneration1Event extends DatacontainerEvent {

	/** serial version for this class. */
	private static final long serialVersionUID = 1L;
	/** Store a hex copy of the epc for comparison. */
	protected String hex = "";

	/**
	 * Constructor.
	 */
	public EPCGeneration1Event() {
		memoryBanks.add(new MemoryBankLengthTuple(new BigInteger("0"), 1));
	}

	/**
	 * Set the epc memory bank.
	 * 
	 * @param memBank
	 * @param length
	 */
	public void setEPCMemory(BigInteger memBank, Integer length) {
		memoryBanks.get(0).setLength(length);
		memoryBanks.get(0).setMemory(memBank);
		hex = memBank.toString(16);
	}

	/**
	 * Returns the epc in hex encoding.
	 * 
	 * @return
	 */
	public String getEpc() {
		return hex;
	}

	/**
	 * Get the epc memory bank.
	 * 
	 * @return
	 */
	public BigInteger getEPCMemory() {
		return memoryBanks.get(0).getMemory();
	}

	/**
	 * Returns the length of the memory bank.
	 * 
	 * @return the epcLength
	 */
	public Integer getEPCMemoryLength() {
		return memoryBanks.get(0).getLength();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EPCGeneration1Event) {
			return hex.hashCode() == obj.hashCode();
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return hex.hashCode();
	}
}
