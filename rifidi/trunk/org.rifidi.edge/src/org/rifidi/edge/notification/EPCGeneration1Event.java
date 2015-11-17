/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
package org.rifidi.edge.notification;

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
	 *            The size of the memory bank in bits
	 */
	public void setEPCMemory(BigInteger memBank, String epc, Integer length) {
		hex = epc;
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
