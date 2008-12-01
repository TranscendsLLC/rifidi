/*
 *  C1G2ReservedBank.java
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
import org.rifidi.edge.tags.util.BitVector;

/**
 * This class models the Reserved Memory Bank (bank 0) on a Gen2 tag
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class C1G2ReservedBank extends MemoryBank {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new Reserved MemoryBank
	 * 
	 * @param bits
	 *            The bits of the memory bank as a binary string composed only
	 *            of '1' and '0' characters. The leftmost bit is at position 0
	 * @throws an
	 *             IllegalArgumentException if the length of bits is not 64
	 */
	public C1G2ReservedBank(String bits) {
		if (bits.length() != 64) {
			throw new IllegalArgumentException(
					"Reserved bank must have 64 bits");
		}
		super.setMemoryBank(bits);
	}

	/**
	 * Get the access password (x20 - x3F)
	 * 
	 * @return bits 32 - 63
	 */
	public BitVector getAccessPwd() {
		return this._bits.get(32, 64);
	}

	/**
	 * Get the kill password (x00 - x1F)
	 * 
	 * @return bits 0 - 31
	 */
	public BitVector getKillPwd() {
		return this._bits.get(0, 32);
	}

}
