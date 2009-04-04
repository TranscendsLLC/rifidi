/**
 * 
 */
package org.rifidi.edge.core.messages;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

/**
 * @author Jochen Mader - jochen@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class DatacontainerEvent implements Serializable {

	/** Serial Version for this class. */
	private static final long serialVersionUID = 1L;

	protected List<BigInteger> memoryBanks;
	
	public BigInteger readMemory(int bankid, int offset, int length) throws IndexOutOfBoundsException{
		BigInteger comp=new BigInteger(Double.toString(Math.pow(2, length)),10);
		return memoryBanks.get(bankid).shiftRight(offset - 1).add(comp);
	}
}
