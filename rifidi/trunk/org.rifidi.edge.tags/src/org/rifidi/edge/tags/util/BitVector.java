/*
 *  BitVector.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.tags.util;

import java.math.BigInteger;

/**
 * The BitVector is a class used to store a vector of bits. The LSB is stored in
 * position 0. Unlike BigInteger, it keeps track of the number of leading 0s, so
 * for example the length of "0011" is 4.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class BitVector {

	/*
	 * Internally, the BitVector is stored as a BigInteger. However, because
	 * BigInteger will truncate leading 0s, a sentinel bit is used to mark the
	 * end of the vector.
	 */
	BigInteger _vector;

	/**
	 * Constructs a BitVector from a string value and a radix. Remember for
	 * binary numbers, the LSB is on the left: the LSB of 1010 is 0.
	 * 
	 * @param val
	 *            the value in string format
	 * @param radix
	 *            the radix
	 */
	public BitVector(String val, int radix) {
		if (2 == radix) {
			// add the sentinel bit
			_vector = new BigInteger("1" + val, 2);
		} else {
			// convert to big integer
			BigInteger bi = new BigInteger(val, radix);

			// convert to binary string, add sentinal, convert back to big
			// integer
			_vector = new BigInteger("1" + bi.toString(2), 2);
		}
	}

	/**
	 * Constructs a BitVector from a string value and a radix. Remember for
	 * binary numbers, the LSB is on the left: the LSB of 1010 is 0.
	 * 
	 * This method ensures that the bitVector is at least
	 * <code>minNumberBits</code> long. If the number of btis occupied by value
	 * is less than this number, this method will pad the bit string with 0s on
	 * the left until it is the minimum size.
	 * 
	 * For example, BitVector(5, 10, 10) will result in this bit String
	 * 0000000101
	 * 
	 * @param val
	 *            the value in string format
	 * @param radix
	 *            the radix
	 * @param minNumberBits
	 *            the minimum number of bits this string should occupy
	 */
	public BitVector(String val, int radix, int minNumberBits) {
		this(val, radix);
		if (this.bitLength() < minNumberBits) {
			int numPadBits = minNumberBits - bitLength();
			StringBuffer buffer = new StringBuffer(this.toString(2));
			for (int i = 0; i < numPadBits; i++) {
				buffer.insert(0, "0");
			}
			_vector = new BigInteger("1" + buffer.toString(), 2);
		}
	}

	public BitVector(BigInteger bigInteger) {
		this(bigInteger, true);
	}

	/**
	 * A private constructor that allows you to choose if you want to add
	 * another sentinel or not
	 * 
	 * @param bigInteger
	 * @param addSentinel
	 *            if true, add a sentinel bit
	 */
	private BitVector(BigInteger bigInteger, boolean addSentinel) {
		if (!addSentinel) {
			_vector = bigInteger;
		} else {
			_vector = new BigInteger("1" + bigInteger.toString(2), 2);
		}
	}

	/**
	 * 
	 * @return the number of bits in this vector
	 */
	public int bitLength() {
		// don't count the sentinal bit
		return _vector.bitLength() - 1;
	}

	/**
	 * Return a new BitVector composed of bits from <code>fromIndex</code>
	 * (inclusive) to <code>toIndex</code>(exclusive).
	 * 
	 * The length of the new BitVetor is <code>toIndex - fromIndex</code>
	 * 
	 * Remember that the LSB is stored at position 0. The LSB in the following
	 * string "1010" is 0. This means getBits(0,3) returns 010.
	 * 
	 * Leading 0s are only kept for numbers with radix 2. For example, the
	 * length of x1F is 5.
	 * 
	 * @param beginIndex
	 *            index of the first bit to include
	 * @param endIndex
	 *            index after the last bit to include
	 * @return a new BitVector from a range in this BitVector
	 * @throws IndexOutOfBoundsException
	 *             if <code>fromIndex</code> is negative, <code>fromIndex</code>
	 *             is larger than <code>toIndex</code>, or <code>toIndex</code>
	 *             is larger than the length of this bitVector
	 */
	public BitVector get(int fromIndex, int toIndex) {
		if (fromIndex < 0) {
			throw new IndexOutOfBoundsException(
					"fromIndex must be greater than 0");
		}
		if (fromIndex > toIndex) {
			throw new IndexOutOfBoundsException(
					"fromIndex cannot be greater than toIndex");
		}
		if (toIndex > bitLength()) {
			throw new IndexOutOfBoundsException(
					"toIndex cannot be greater than bitLength()");
		}

		StringBuffer sb = new StringBuffer();
		for (int i = fromIndex; i < toIndex; i++) {
			if (_vector.testBit(i)) {
				sb.append("1");
			} else {
				sb.append("0");
			}
		}
		return new BitVector(sb.toString(), 2);
	}

	/**
	 * Gets a single bit at the specified index
	 * 
	 * @param index
	 *            the index to test
	 * @return true if the bit at the index is 1, false otherwise
	 * @throws IndexOutOfBoundsException
	 *             if <code>index<0</code> or if
	 *             <code>index > bitLength() -1</code>
	 */
	public boolean get(int index) {
		if (index < 0) {
			throw new IndexOutOfBoundsException("index must be greater than 0");
		}
		if (index > bitLength() - 1) {
			throw new IndexOutOfBoundsException(
					"index must be less than bitLength() - 1");
		}
		return _vector.testBit(index);
	}

	/**
	 * A private method to return the underlying BigInteger. For use in static
	 * methods
	 * 
	 * @return the underlying big integer
	 */
	private BigInteger getBigInt() {
		return _vector;
	}

	/**
	 * Returns this BitVector as a number in the base of radix
	 * 
	 * @param radix
	 *            the base of the number
	 * @return the number as a base radix number
	 */
	public String toString(int radix) {
		// convert to a binary string and remove sentinel
		String bitString = this.getBigInt().toString(2).substring(1);

		if (2 == radix) {
			return bitString;
		} else {
			// convert binary string to bigInteger and convert that to the given
			// radix
			return new BigInteger(bitString, 2).toString(radix);
		}
	}

	/**
	 * Returns this BitVector as a number in the base of radix. It also assures
	 * that the number has at least <code>minDigits</code> number of digits. If
	 * the number of digits is less, it left-pads the number with 0s
	 * 
	 * @param radix
	 *            the base of the number
	 * @param minDigits
	 *            the minimum number of digits the new number should have.
	 * @return the number as a bas radix number, with at least
	 *         <code>minDigits</code> number of digits
	 */
	public String toString(int radix, int minDigits) {
		String number = this.toString(radix);
		StringBuilder sb = new StringBuilder(number);
		while (sb.length() < minDigits) {
			sb.insert(0, "0");
		}
		return sb.toString();
	}

	/**
	 * A helper method that convert the BitVector to a string
	 * 
	 * @param bitVector
	 *            The bit
	 * @param radix
	 *            the base
	 * @return A string that represents the
	 */
	public static String toString(BitVector bitVector, int radix) {
		return bitVector.toString(radix);
	}

	/**
	 * If this int is bigger than 32 bits, only the low-order 32 bits will be
	 * returned
	 * 
	 * @return the int value for this bitVector.
	 */
	public int intValue() {
		String bitString = _vector.toString(2).substring(1);
		return new BigInteger(bitString, 2).intValue();
	}
}
