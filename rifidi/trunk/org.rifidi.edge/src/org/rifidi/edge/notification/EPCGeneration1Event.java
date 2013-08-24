/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.notification;

import java.math.BigInteger;

import org.apache.commons.codec.binary.Hex;

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
	 *            The size of the memory bank in bits
	 */
	public void setEPCMemory(BigInteger memBank, Integer length) {
		hex = new String(Hex.encodeHex(memBank.toByteArray()));
		memoryBanks.get(0).setLength(length);
		memoryBanks.get(0).setMemory(memBank);
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
	 * @return the number of bits in the EPC
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.notification.data.DatacontainerEvent#getID
	 * ()
	 */
	@Override
	public BigInteger getID() {
		return this.memoryBanks.get(0).getMemory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.services.notification.data.DatacontainerEvent#
	 * getFormattedID()
	 */
	@Override
	public String getFormattedID() {
		return getEpc();
	}

}
