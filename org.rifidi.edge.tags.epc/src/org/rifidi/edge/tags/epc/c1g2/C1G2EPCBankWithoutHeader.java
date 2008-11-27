/*
 *  C1G2EPCBankWithoutHeader.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.tags.epc.c1g2;

import org.rifidi.edge.tags.exceptions.IllegalBankAccessException;
import org.rifidi.edge.tags.util.BitVector;
import org.rifidi.edge.tags.util.ConvertingUtil;

/**
 * This class models the EPC Memory Bank (bank 1) on a Gen2 tag. It is used when
 * the tag read that this object was created from does not contain the header
 * information.
 * 
 * Note that for this class, only the EPC is stored in the underlying memory
 * bank. The PCBIts are stored in this class and will not affect any superclass
 * methods
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class C1G2EPCBankWithoutHeader extends AbstractC1G2EPCBank {

	/**
	 * Number of bits in NSI field in PC
	 */
	private final static int NSI_LENGTH = 9;
	/**
	 * Number of bits in length field of PC
	 */
	private final static int LENGTH_FIELD_LENGTH = 5;
	private final static String DEFAULT_RFU_BITS = "00";
	private final static String DEFAULT_TOGGLE_BIT = "0";
	private final static String DEFAULT_AFI_BITS = "00000000";

	private BitVector _pcBits;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Create a new C1G2 EPC MemoryBank without knowing the Header bits (the CRC
	 * and PC Bits). The NSI bits will be assumed to be the default (000000000),
	 * and the length field of the PC_Bits will be CEILING(LENGTH(EPC)/16).
	 * 
	 * The EPC is assumed to be an EPCGlobal Application and a default Numbering
	 * System Identifier (NSI) bit string will be used (000000000). Specifically
	 * this means the toggle bit (bit x17) and the AFI bits (18h-1Fh) are zero.
	 * Use this if you know the EPC is encoded via a known scheme as defined by
	 * the Tag Data Specification (TDS) (e.g. SGTIN, GID, DOD).
	 * 
	 * See section 6.3.2.1.2.2 in the Gen2 Specification or section 3.1-3.2 in
	 * the Tag Data Specification 1.4 for more details
	 * 
	 * @param epc
	 *            the EPC bits
	 */
	public C1G2EPCBankWithoutHeader(String EPC) {
		super(EPC);
		_pcBits = createPCBits(EPC.length(), DEFAULT_TOGGLE_BIT
				+ DEFAULT_AFI_BITS);
	}

	/**
	 * Create a new C1G2 EPC MemoryBank without knowing the Header bits (the CRC
	 * and PC Bits). The NSI bits will be assumed to be the default (000000000),
	 * and the length field of the PC_Bits will be CEILING(LENGTH(EPC)/16).
	 * 
	 * Use this constructor if you need to specify the Numbering System
	 * Identifier (NSI) bits. The NSI bits are 9 bits (bits 17h-1Fh on a Gen2
	 * Tag)that define the Application Family of the EPC. If the toggle bit (bit
	 * 17h) is a 1, then the AFI bits are under the authority of ISO.
	 * 
	 * This constructor should be used if you know that your tag does not follow
	 * a defined EPC-defined encoding scheme as defined by the Tag Data
	 * Specification (TDS) (e.g. SGTIN, GID, DOD).
	 * 
	 * See section 6.3.2.1.2.2 in the Gen2 Specification or section 3.1-3.2 in
	 * the Tag Data Specification 1.4 for more details
	 * 
	 * @param epc
	 *            the EPC bits
	 * @param NSI
	 *            the 9 NSI bits
	 */
	public C1G2EPCBankWithoutHeader(String EPC, String NSI) {
		super(EPC);
		_pcBits = createPCBits(EPC.length(), NSI);
	}

	/**
	 * A helper method to create a default PCBits vector for this class
	 * 
	 * @param EPCLength
	 *            the number of bits in the EPC
	 * @param NSIBits
	 *            The NSI Bits must contain 9 bits
	 * @return a BitVector that represents the PC bits
	 */
	private BitVector createPCBits(int EPCLength, String NSIBits) {
		if (EPCLength < 0) {
			throw new IllegalArgumentException(
					"number of EPCBits must at least 0");
		}

		if (EPCLength > 496) {
			throw new IllegalArgumentException(
					"number of EPCBits must be less than 496");
		}
		if (NSIBits.length() != NSI_LENGTH) {
			throw new IllegalArgumentException("number of bits in NSI must be "
					+ NSI_LENGTH);
		}

		Integer numBlocks = ConvertingUtil.roundUpDivision(EPCLength, 16);

		// create length bits that are 5 bits long
		String lengthBits = new BitVector(numBlocks.toString(), 10,
				LENGTH_FIELD_LENGTH).toString(2);

		String bits = lengthBits + DEFAULT_RFU_BITS + NSIBits;

		// need to reverse the bits so that bit 0 is the left-most bit
		return new BitVector(new StringBuilder(bits).reverse().toString(), 2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.tags.epc.c1g2.AbstractC1G2EPCBank#getAFIBits()
	 */
	@Override
	public BitVector getAFIBits() throws IllegalBankAccessException {
		return _pcBits.get(8, 16);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.tags.epc.c1g2.AbstractC1G2EPCBank#getCRCBits()
	 */
	@Override
	public BitVector getCRCBits() throws IllegalBankAccessException {
		throw new IllegalBankAccessException("CRC Bits not available");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.tags.epc.c1g2.AbstractC1G2EPCBank#getEPCBits()
	 */
	@Override
	public BitVector getEPCBits() throws IllegalBankAccessException {
		try {
			return _bits.get(0, _bits.bitLength());
		} catch (IndexOutOfBoundsException ex) {
			throw new IllegalBankAccessException("EPC bits not available");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.tags.epc.c1g2.AbstractC1G2EPCBank#getLengthBits()
	 */
	@Override
	public BitVector getLengthBits() throws IllegalBankAccessException {
		return _pcBits.get(0, 5);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.tags.epc.c1g2.AbstractC1G2EPCBank#getPCBits()
	 */
	@Override
	public BitVector getPCBits() throws IllegalBankAccessException {
		return _pcBits.get(0, _pcBits.bitLength());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.tags.epc.c1g2.AbstractC1G2EPCBank#getRFUBits()
	 */
	@Override
	public BitVector getRFUBits() throws IllegalBankAccessException {
		return _pcBits.get(5, 7);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.tags.epc.c1g2.AbstractC1G2EPCBank#getToggleBit()
	 */
	@Override
	public Boolean getToggleBit() throws IllegalBankAccessException {
		return _pcBits.get(7);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.tags.epc.c1g2.AbstractC1G2EPCBank#getNSIAndEPCBits()
	 */
	@Override
	public BitVector getNSIAndEPCBits() throws IllegalBankAccessException {
		try {
			BitVector NSIVector = _pcBits.get(7, 16);
			return NSIVector.append(_bits);
		} catch (IndexOutOfBoundsException ex) {
			throw new IllegalBankAccessException("EPC bits not available");
		}
	}

}
