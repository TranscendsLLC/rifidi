/*
 *  PCBits.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.tags.encodings.epc.data;

import org.rifidi.edge.tags.util.BitVector;
import org.rifidi.edge.tags.util.ConvertingUtil;

/**
 * This class represents the PC bits in the EPC bank of Gen2 tags. It is
 * sometimes needed to convert EPC tags, for example, when the toggle bit=1.
 * 
 * For a complete explanation of the PC bits, see section 3.2.1 in the TDS from
 * EPCGlobal.
 * 
 * If the PCBits are not needed or not collected and the EPC is known to be
 * encoded in an EPC format, use the constructor that takes in the number of EPC
 * bits in the encoding
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class PCBits {

	/**
	 * The number of bits in the PC Bits
	 */
	private static int LENGTH_OF_PC_BITS = 16;

	/**
	 * The number of Length bits in the PC bits
	 */
	private static int NUMBER_OF_LENGTH_BITS = 5;

	/**
	 * A default string for the 11 other bits (non length bits) in the PC bits
	 */
	private static String OTHER_BITS = "00000000000";

	private BitVector _bits;

	/**
	 * Create a new PCBits object from the pc bit string. The PC bits have 16
	 * bits in it
	 * 
	 * @param bits
	 *            the PCBits
	 * @throws IllegalArgumentException
	 *             if the number of bits is not 16
	 */
	public PCBits(String bits) {
		if (null == bits) {
			throw new IllegalArgumentException("bits cannot be null");
		}
		if (LENGTH_OF_PC_BITS != bits.length()) {
			throw new IllegalArgumentException("PC bits must contain 16 bits");
		}
		// need to reverse the bits so that bit 0 is the left-most bit
		_bits = new BitVector(new StringBuilder(bits).reverse().toString(), 2);
	}

	/**
	 * Constructs a default PCBits object given the number of bits used by the
	 * EPC. The length bits will be set to
	 * <code>CEILING(numberOfEPCBits/16)</code>. The rest of the bits (NSI,
	 * Toggle, and Reserved) will be set to 0.
	 * 
	 * @param numberOfEPCBits
	 *            The number of bits occupied by the EPC. Must be between 0 and
	 *            16.
	 * @throws IllegalArgumentException
	 *             if numberOfEPCBits is less than 0 or greater than 496
	 */
	public PCBits(int numberOfEPCBits) {
		if (numberOfEPCBits < 0) {
			throw new IllegalArgumentException(
					"number of EPCBits must at least 0");
		}

		if (numberOfEPCBits > 496) {
			throw new IllegalArgumentException(
					"number of EPCBits must be less than 496");
		}
		Integer numBlocks = ConvertingUtil.roundUpDivision(numberOfEPCBits, 16);

		// create length bits that are 5 bits long
		String lengthBits = new BitVector(numBlocks.toString(), 10,
				NUMBER_OF_LENGTH_BITS).toString(2);

		String bits = lengthBits + OTHER_BITS;

		// need to reverse the bits so that bit 0 is the left-most bit
		_bits = new BitVector(new StringBuilder(bits).reverse().toString(), 2);

	}

	/**
	 * Return the length bits. This is the number of 16-bit blocks the EPC
	 * occupies
	 * 
	 * 
	 * 
	 * @return bits 0 - 4
	 */
	public BitVector getLengthBits() {
		return _bits.get(0, 5);
	}

	/**
	 * According to 3.2.2. of the TDS spec, the length field of the EPCGen tag
	 * is the the number of 16-bit segments occupied with valid data, not
	 * including the CTC, minus one.
	 * 
	 * When a Gen 2 Tag contains an EPC Tag Encoding in the EPC bank, the length
	 * field is normally set to the smallest number that would contain the
	 * particular kind of EPC Tag Encoding in use. Specifically, if the EPC bank
	 * contains an N-bit EPC Tag Encoding, then the length field is normally set
	 * to N/16, rounded up to the nearest integer. For example, with a 96-bit
	 * EPC Tag Encoding, the length field is normally set to 6 (00110 in binary)
	 * 
	 * @return (the number of bits) * 16
	 */
	public int getLength() {
		return (_bits.get(0, 5).intValue()) * 16;
	}

	/**
	 * Return the reserved for future use bits
	 * 
	 * @return bits 5 - 6
	 */
	public BitVector getRFUBits() {
		return _bits.get(5, 7);
	}

	/**
	 * Returns the toggle bit
	 * 
	 * @return bit 7
	 */
	public boolean getToggleBit() {
		return _bits.get(7);
	}

	/**
	 * Returns the Reserved/AFI bits
	 * 
	 * @return bits 8 - 15
	 */
	public BitVector getAFIBits() {
		return _bits.get(8, 16);
	}

	/**
	 * Get all the PC bits
	 * 
	 * @return bits 0-15
	 */
	public BitVector getBits() {
		return _bits.get(0, 16);
	}

	@Override
	public String toString() {
		return new StringBuilder(_bits.toString(2)).reverse().toString();
	}

}
