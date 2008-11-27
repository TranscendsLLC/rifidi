/*
 *  AbstractC1G2EPCBank.java
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
 * This class serves as a base class for C1G2EPC Memory Bank (bank 1).
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class AbstractC1G2EPCBank extends MemoryBank {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Create a new C1G2 Memory Bank
	 * 
	 * @param bits
	 *            a String of '1' and '0' bits
	 */
	public AbstractC1G2EPCBank(String bits) {
		super(bits);
	}

	/**
	 * Gets the StoredCRC bits (x00 to x0F)
	 * 
	 * @return bits 0 - 15
	 */
	public abstract BitVector getCRCBits() throws IllegalBankAccessException;

	/**
	 * Gets the StoredPC bits (x10 to x1F)
	 * 
	 * @return bits 16-31
	 */
	public abstract BitVector getPCBits() throws IllegalBankAccessException;

	/**
	 * Gets the EPC length bits (x10 to x14). Represents the number of 16-bit
	 * words comprising the PC bits and EPC bits
	 * 
	 * @return bits 16-20
	 */
	public abstract BitVector getLengthBits() throws IllegalBankAccessException;

	/**
	 * Gets Reserved for Future Use bits
	 * 
	 * @return bits 21-22
	 */
	public abstract BitVector getRFUBits() throws IllegalBankAccessException;

	/**
	 * Gets Toggle Bit (x17). If false, then the EPC is part of the EPC tag
	 * encoding scheme (as defined in the Tag Data Standard) and bits x18 to x1F
	 * are Reserved for future use. If true then the EPC is not part of the EPC
	 * tag encoding scheme, and bits x18 to x1F are Application Family
	 * Identifier (AFI) bits as defined by ISO 15961
	 * 
	 * @return return bit 23
	 */
	public abstract Boolean getToggleBit() throws IllegalBankAccessException;

	/**
	 * Gets the Reserved/AFI bits (x18 - x1F). If the toggle bits is false, then
	 * this field is reserved must be set to all 0s. If the toggle bit is true,
	 * this field is an Application Family Identifier (AFI) and is defined as
	 * part of ISO 15961
	 * 
	 * @return bits 24-31
	 */
	public abstract BitVector getAFIBits() throws IllegalBankAccessException;

	/**
	 * Gets Numbering System Identifier Bits. Specifically: the
	 * Toggle bit (x17), the Reserved/AFI bits (x18-1F) 
	 * 
	 * @return bits 23-32
	 * @throws IllegalBankAccessException
	 */
	public abstract BitVector getNSIBits()
			throws IllegalBankAccessException;

	/**
	 * Returns the EPC bits (starting at x20).
	 * 
	 * @return bits starting at 32
	 * @throws IllegalBankAccessException
	 *             If no EPC bits are available
	 */
	public abstract BitVector getEPCBits() throws IllegalBankAccessException;

}
