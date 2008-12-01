/*
 *  C1G2TIDBank.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.tags.epc.c1g2;

import org.rifidi.edge.tags.data.memorybank.MemoryBank;
import org.rifidi.edge.tags.exceptions.IllegalBankAccessException;
import org.rifidi.edge.tags.util.BitVector;

/**
 * This class models the TID Memory Bank (bank 2) on a Gen2 tag
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class C1G2TIDBank extends MemoryBank {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Create a new TID bank
	 * 
	 * @param bits
	 *            The bits of the memory bank as a binary string composed only
	 *            of '1' and '0' characters. The leftmost bit is at position 0
	 * @throws IllegalArgumentException
	 *             if the number of bits is less than 8
	 */
	public C1G2TIDBank(String bits) {
		if (bits.length() < 8) {
			throw new IllegalArgumentException(
					"TID Bank must contain at least 8 bits");
		}
		super.setMemoryBank(bits);
	}

	/**
	 * Returns the ISO/IEC 15963 allocation class identifier (x00 - x07).
	 * 
	 * @return bits 0 - 7
	 */
	public BitVector getAllocationClassIdentifierBits() {
		return _bits.get(0, 8);
	}

	/**
	 * Returns the vendor-defined bits (x08 and above)
	 * 
	 * @return bits 8 and above
	 * @throws IllegalBankAccessException
	 *             if the vendor defined bits are not available
	 */
	public BitVector getVendorDefinedBits() throws IllegalBankAccessException {
		try {
			return _bits.get(8, _bits.bitLength());
		} catch (IndexOutOfBoundsException ex) {
			throw new IllegalBankAccessException(
					"Vendor Defined Bits are not available");
		}
	}

}
