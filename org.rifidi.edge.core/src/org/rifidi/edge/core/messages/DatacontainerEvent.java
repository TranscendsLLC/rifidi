/**
 * 
 */
package org.rifidi.edge.core.messages;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jochen Mader - jochen@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
 */
public abstract class DatacontainerEvent implements Serializable {

	/** Serial Version for this class. */
	private static final long serialVersionUID = 1L;
	protected List<MemoryBankLengthTuple> memoryBanks = new ArrayList<MemoryBankLengthTuple>();

	public BigInteger readMemory(int bankid, int offset, int length)
			throws IndexOutOfBoundsException {
		BigInteger comp = new BigInteger(Double.toString(Math.pow(2, length)),
				10);
		return memoryBanks.get(bankid).getMemory().shiftRight(offset - 1).add(
				comp);
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
	 * 
	 */
	public class MemoryBankLengthTuple {
		/** The memory bank. */
		private BigInteger memory;
		/** Length of the memory bank. */
		private Integer length;

		/**
		 * @param memory
		 * @param length
		 */
		public MemoryBankLengthTuple(BigInteger memory, Integer length) {
			super();
			this.memory = memory;
			this.length = length;
		}

		/**
		 * @return the memory
		 */
		public BigInteger getMemory() {
			return memory;
		}

		/**
		 * @param memory
		 *            the memory to set
		 */
		public void setMemory(BigInteger memory) {
			this.memory = memory;
		}

		/**
		 * @return the length
		 */
		public Integer getLength() {
			return length;
		}

		/**
		 * @param length
		 *            the length to set
		 */
		public void setLength(Integer length) {
			this.length = length;
		}

	}
}
