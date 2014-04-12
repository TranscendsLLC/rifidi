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

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that serves as a base class for representing the data physically stored
 * on a tag. It uses the metaphore of MemoryBanks and assumes that tags stores
 * data on banks of memory. It can have 0 to many memory banks.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
 */
public abstract class DatacontainerEvent implements Serializable {

	/** Serial Version for this class. */
	private static final long serialVersionUID = -3904919665346333337L;
	/** The set of memory banks that this Tag has */
	protected List<MemoryBankLengthTuple> memoryBanks = new ArrayList<MemoryBankLengthTuple>();

	/**
	 * Read the content of a bank
	 * 
	 * @param bankID
	 *            The ID of the bank to read
	 * @param offset
	 *            The offset (in bits)
	 * @param length
	 *            The number of bits to read
	 * @return The bits that were read
	 * @throws IndexOutOfBoundsException
	 *             if there was a problem accessing a bank or a memory location
	 */
	public BigInteger readMemory(int bankID, int offset, int length)
			throws IndexOutOfBoundsException {
		BigInteger comp = new BigInteger(Double.toString(Math.pow(2, length)),
				10);
		return memoryBanks.get(bankID).getMemory().shiftRight(offset - 1).add(
				comp);
	}

	/**
	 * @param bankID
	 *            The ID of the memory Bank
	 * @return The memory bank specified
	 */
	public MemoryBankLengthTuple getMemoryBank(Integer bankID) {
		return memoryBanks.get(bankID);
	}

	/**
	 * Read from a memory bank.
	 * 
	 * @param address
	 *            address: @[bankID].[length](.[offset])
	 * @return The contents of the specified address as a hex string
	 */
	public String readMemory(String address) {
		String[] addrArray = address.substring(1).split(".");
		if (addrArray.length == 3) {
			return readMemory(Integer.parseInt(addrArray[0]),
					Integer.parseInt(addrArray[2]),
					Integer.parseInt(addrArray[1])).toString(16);
		}
		return readMemory(Integer.parseInt(addrArray[0]), 0,
				Integer.parseInt(addrArray[1])).toString(16);
	}

	/**
	 * Class for holding a bank and its length. Required because BigInteger
	 * swallows zeroes.
	 * 
	 * @author Jochen Mader - jochen@pramari.com
	 */
	public class MemoryBankLengthTuple implements Serializable {
		/** Default. */
		private static final long serialVersionUID = 1L;
		/** The memory bank. */
		private BigInteger memory;
		/** Length of the memory bank. */
		private Integer length;

		/**
		 * Constructor.
		 * 
		 * @param memory
		 * @param length
		 *            in bits
		 */
		public MemoryBankLengthTuple(BigInteger memory, Integer length) {
			super();
			this.memory = memory;
			this.length = length;
		}

		/**
		 * Returns the memory location of this tuple.
		 * 
		 * @return the memory
		 */
		public BigInteger getMemory() {
			return memory;
		}

		/**
		 * Sets the memory location.
		 * 
		 * @param memory
		 *            the memory to set
		 */
		public void setMemory(BigInteger memory) {
			this.memory = memory;
		}

		/**
		 * Gets the length in bits of the tuple.
		 * 
		 * @return the length in bits
		 */
		public Integer getLength() {
			return length;
		}

		/**
		 * Sets the length of the tuple in bits.
		 * 
		 * @param length
		 *            the length to set
		 */
		public void setLength(Integer length) {
			this.length = length;
		}
	}

	/**
	 * Returns the tag identity information.
	 * 
	 * @return
	 */
	public abstract BigInteger getID();

	/**
	 * Get the ID as a formatted number. This string can include leading 0s if
	 * necessary.
	 * 
	 * @return
	 */
	public abstract String getFormattedID();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getFormattedID();
	}

}
