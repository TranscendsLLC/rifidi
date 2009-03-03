/**
 * 
 */
package org.rifidi.edge.core.messages;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.rifidi.edge.core.messages.exceptions.IllegalBankAccessException;

/**
 * @author Jochen Mader - jochen@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class DatacontainerEvent implements Serializable {

	/** Serial Version for this class. */
	private static final long serialVersionUID = 1L;
	/** Memory banks of the data container. */
	private List<BigInteger> memoryBanks;

	/**
	 * Constructor.
	 * 
	 * @param banks
	 *            number of memory banks to create.
	 */
	public DatacontainerEvent(int banks) {
		memoryBanks = new ArrayList<BigInteger>();
		for (int count = 0; count < banks; count++) {
			memoryBanks.add(new BigInteger("0", 2));
		}
	}

	protected BigInteger readMemory(int bankid, int offset, int length) {
		return memoryBanks.get(bankid).shiftRight(offset - 1).mod(
				BigInteger.valueOf((long) Math.pow(2, length)));
	}

	/**
	 * Overwrite the memory bank.
	 * 
	 * @param bankid
	 * @param values
	 * @throws IllegalBankAccessException
	 */
	protected void setMemory(int bankid, BigInteger values) {
		memoryBanks.set(bankid, values);
	}
	
	protected BigInteger getMemoryBank(int bankid){
		return memoryBanks.get(bankid);
	}
}
