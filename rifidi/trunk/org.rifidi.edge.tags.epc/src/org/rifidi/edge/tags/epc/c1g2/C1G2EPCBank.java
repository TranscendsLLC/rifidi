/*
 *  C1G2EPCBank.java
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
 * This class models the EPC Memory Bank (bank 1) on a Gen2 tag
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class C1G2EPCBank extends MemoryBank {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new C1G2EPCBank.
	 * 
	 * @param bits
	 *            The bits of the memory bank as a binary string composed only
	 *            of '1' and '0' characters. The leftmost bit is at position 0
	 * @throws IllegalArgumentException
	 *             if the number of bits is less than 32
	 */
	public C1G2EPCBank(String bits) {
		super(bits);
		if (_bits.bitLength() <= 32) {
			throw new IllegalArgumentException(
					"EPC Bank must be at least 32 bits long");
		}
	}

	/**
	 * Gets the StoredCRC bits (x00 to x0F)
	 * 
	 * @return bits 0 - 15
	 */
	public BitVector getCRCBits() {
		return _bits.get(0, 16);
	}

	/**
	 * Gets the StoredPC bits (x10 to x1F)
	 * 
	 * @return bits 16-31
	 */
	public BitVector getPCBits() {
		return _bits.get(16, 32);
	}

	/**
	 * Gets the EPC length bits (x10 to x14). Represents the number of 16-bit
	 * words comprising the PC bits and EPC bits
	 * 
	 * @return bits 16-20
	 */
	public BitVector getLengthBits() {
		return _bits.get(16, 21);
	}

	/**
	 * Gets Reserved for Future Use bits
	 * 
	 * @return bits 21-22
	 */
	public BitVector getRFUBits() {
		return _bits.get(21, 23);
	}

	/**
	 * Gets Toggle Bit (x17). If false, then the EPC is part of the EPC tag
	 * encoding scheme (as defined in the Tag Data Standard) and bits x18 to x1F
	 * are Reserved for future use. If true then the EPC is not part of the EPC
	 * tag encoding scheme, and bits x18 to x1F are Application Family
	 * Identifier (AFI) bits as defined by ISO 15961
	 * 
	 * @return return bit 23
	 */
	public Boolean getToggleBit() {
		return _bits.get(23);
	}

	/**
	 * Gets the Reserved/AFI bits (x18 - x1F). If the toggle bits is false, then
	 * this field is reserved must be set to all 0s. If the toggle bit is true,
	 * this field is an Application Family Identifier (AFI) and is defined as
	 * part of ISO 15961
	 * 
	 * @return bits 24-31
	 */
	public BitVector getAFIBits() {
		return _bits.get(24, 32);
	}

	/**
	 * Returns the EPC bits (starting at x20).
	 * 
	 * @return bits starting at 32
	 * @throws IllegalBankAccessException
	 *             If no EPC bits are available
	 */
	public BitVector getEPCBits() throws IllegalBankAccessException {
		try {
			return _bits.get(32, _bits.bitLength());
		} catch (IndexOutOfBoundsException ex) {
			throw new IllegalBankAccessException("EPC bits not available");
		}
	}
}
