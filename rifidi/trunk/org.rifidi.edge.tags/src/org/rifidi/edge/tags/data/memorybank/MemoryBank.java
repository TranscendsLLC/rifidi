/*
 *  MemoryBank.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.tags.data.memorybank;

import java.io.Serializable;

import org.rifidi.edge.tags.exceptions.IllegalBankAccessException;
import org.rifidi.edge.tags.util.BitVector;

/**
 * This class can be used to represent a memory bank on a tag. Other classes are
 * encouraged to extend this class to provide methods that return logical chunks
 * of bits
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class MemoryBank implements Serializable {

	/**
	 * The serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The bits on the memory bank. Please read the documentation on BitVector
	 */
	protected BitVector _bits;

	/**
	 * Sets the bits for this MemoryBank. The bitString should be in binary
	 * format (composed only of '1' and '0' characters).
	 * 
	 * The bits should be logically structured so that the left-most bit is
	 * indexed at 0
	 * 
	 * This method should probably be called by the constructor in the subclass
	 * of this class.
	 * 
	 * @param bits
	 *            The bits on the memory bank
	 * @throws IllegalArgumentException
	 *             if bits is null or of the string contains anything other than
	 *             a '1' or a '0' character
	 */
	protected void setMemoryBank(String bits) {
		if (null == bits) {
			throw new IllegalArgumentException("bits cannot be null");
		}

		// reverse the bits, because BitVector indexes the bits where the
		// rightmost bit is at index 0
		_bits = new BitVector(new StringBuffer(bits).reverse().toString(), 2);
	}

	/**
	 * 
	 * @return The capacity of this memory bank
	 */
	public int getMemoryBankSize() {
		return _bits.bitLength();
	}

	/**
	 * This method simulates a memory access from a bank.
	 * 
	 * @param bits
	 *            the number of bits to read
	 * @param offset
	 *            the offset from the first bit. 0 indicates no offset
	 * @return A BitVector representing <code>bits</code> number of bits
	 *         starting at position <code>offset</code>
	 * @throws IllegalBankAccessException
	 *             If there was an out of bounds exception while performing the
	 *             access
	 */
	public BitVector access(int bits, int offset)
			throws IllegalBankAccessException {
		try {
			return _bits.get(offset, offset + bits);
		} catch (IndexOutOfBoundsException ex) {
			throw new IllegalBankAccessException();
		}
	}

	@Override
	public String toString() {
		String bits = _bits.toString(2);
		return new StringBuffer(bits).reverse().toString();
	}

}
