/*
 *  C1G1EPCBank.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.tags.epc.c1g1;

import org.rifidi.edge.tags.data.memorybank.MemoryBank;
import org.rifidi.edge.tags.exceptions.IllegalBankAccessException;
import org.rifidi.edge.tags.util.BitVector;

/**
 * This class models the EPC Memory Bank on a Gen1 tag.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class C1G1EPCBank extends MemoryBank {

	private static final int HEADER_BITS = 0;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param bits
	 */
	public C1G1EPCBank(String bits) {
		if (bits.length() < HEADER_BITS) {
			throw new IllegalArgumentException("bits must contain at least "
					+ HEADER_BITS + " bits");
		}
		super.setMemoryBank(bits);
	}

	public BitVector getEPC() throws IllegalBankAccessException {
		try {
			return _bits.get(HEADER_BITS, _bits.bitLength());
		} catch (IndexOutOfBoundsException ex) {
			throw new IllegalBankAccessException();
		}
	}
}
