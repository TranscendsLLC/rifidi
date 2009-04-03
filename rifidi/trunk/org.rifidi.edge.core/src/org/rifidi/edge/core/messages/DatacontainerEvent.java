/**
 * 
 */
package org.rifidi.edge.core.messages;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * @author Jochen Mader - jochen@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class DatacontainerEvent implements Serializable {

	/** Serial Version for this class. */
	private static final long serialVersionUID = 1L;

	protected BigInteger readMemory(BigInteger bank, int offset, int length) {
		return bank.shiftRight(offset - 1).mod(
				BigInteger.valueOf((long) Math.pow(2, length)));
	}
	
	protected BigInteger createMemoryBank(){
		return new BigInteger("0",2);
	}
}
