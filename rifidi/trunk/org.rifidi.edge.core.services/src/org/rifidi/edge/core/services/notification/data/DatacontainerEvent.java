/*
 * 
 * DatacontainerEvent.java
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

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Object that services as a basis for a tag read
 * 
 * @author Jochen Mader - jochen@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
 */
public abstract class DatacontainerEvent implements Serializable {

	/** Serial Version for this class. */
	private static final long serialVersionUID = 1L;
	protected List<MemoryBankLengthTuple> memoryBanks = new ArrayList<MemoryBankLengthTuple>();

	/**
	 * Read the content of a bank
	 * 
	 * @param bankid
	 *            The ID of the bank to read
	 * @param offset
	 *            The offset (in bits)
	 * @param length
	 *            The number of bits to read
	 * @return The bits that were read
	 * @throws IndexOutOfBoundsException
	 *             if there was a problem accessing a bank or a memory location
	 */
	public BigInteger readMemory(int bankid, int offset, int length)
			throws IndexOutOfBoundsException {
		BigInteger comp = new BigInteger(Double.toString(Math.pow(2, length)),
				10);
		return memoryBanks.get(bankid).getMemory().shiftRight(offset - 1).add(
				comp);
	}

	/**
	 * Get a memory bank.
	 * 
	 * @param id
	 * @return
	 */
	public MemoryBankLengthTuple getMemoryBank(Integer id) {
		return memoryBanks.get(id);
	}

	/**
	 * Read from a memory bank.
	 * 
	 * @param address
	 *            address: @<bankid>.<length>(.<offset>)
	 * @return
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
	 * Class for holding a bank and it's length. Required because BigInteger
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
		 * Gets the length of the tuple.
		 * 
		 * @return the length
		 */
		public Integer getLength() {
			return length;
		}

		/**
		 * Sets the length of the tuple.
		 * 
		 * @param length
		 *            the length to set
		 */
		public void setLength(Integer length) {
			this.length = length;
		}

	}
}
