/**
 * 
 */
package org.rifidi.edge.core.messages;

import java.math.BigInteger;

/**
 * @author Jochen Mader - jochen@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class EPCGeneration2Event extends EPCGeneration1Event {

	/** Serial Version ID for this class */
	private static final long serialVersionUID = 1L;
	private BigInteger reservedBank;
	private BigInteger tidBank;
	private BigInteger userMemoryBank;

	public EPCGeneration2Event() {
		super();
		this.reservedBank = super.createMemoryBank();
		this.tidBank = super.createMemoryBank();
		this.userMemoryBank = super.createMemoryBank();
	}

	public void setReservedMemory(BigInteger memory) {
		reservedBank = memory;
	}

	public void setTIDMemory(BigInteger memory) {
		tidBank = memory;
	}

	public void setUserMemory(BigInteger memory) {
		userMemoryBank = memory;
	}

}
