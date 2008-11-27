/*
 *  C1G2EPCBankWithHeader.java
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

/**
 * This class models the EPC Memory Bank (bank 1) on a Gen2 tag. It is used when
 * the tag read that this object was created from also has the header
 * information (first 32 bits).
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class C1G2EPCBankWithHeader extends AbstractC1G2EPCBank {

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
	public C1G2EPCBankWithHeader(String bits) {
		super(bits);
		if (_bits.bitLength() <= 32) {
			throw new IllegalArgumentException(
					"EPC Bank must be at least 32 bits long");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.tags.epc.c1g2.AbstractC1G2EPCBank#getCRCBits()
	 */
	@Override
	public BitVector getCRCBits() {
		return _bits.get(0, 16);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.tags.epc.c1g2.AbstractC1G2EPCBank#getPCBits()
	 */
	@Override
	public BitVector getPCBits() {
		return _bits.get(16, 32);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.tags.epc.c1g2.AbstractC1G2EPCBank#getLengthBits()
	 */
	@Override
	public BitVector getLengthBits() {
		return _bits.get(16, 21);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.tags.epc.c1g2.AbstractC1G2EPCBank#getRFUBits()
	 */
	@Override
	public BitVector getRFUBits() {
		return _bits.get(21, 23);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.tags.epc.c1g2.AbstractC1G2EPCBank#getToggleBit()
	 */
	@Override
	public Boolean getToggleBit() {
		return _bits.get(23);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.tags.epc.c1g2.AbstractC1G2EPCBank#getAFIBits()
	 */
	@Override
	public BitVector getAFIBits() {
		return _bits.get(24, 32);
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.tags.epc.c1g2.AbstractC1G2EPCBank#getNSIAndEPCBits()
	 */
	@Override
	public BitVector getNSIAndEPCBits() throws IllegalBankAccessException {
		try {
			return _bits.get(23, _bits.bitLength());
		} catch (IndexOutOfBoundsException ex) {
			throw new IllegalBankAccessException("EPC bits not available");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.tags.epc.c1g2.AbstractC1G2EPCBank#getEPCBits()
	 */
	@Override
	public BitVector getEPCBits() throws IllegalBankAccessException {
		try {
			return _bits.get(32, _bits.bitLength());
		} catch (IndexOutOfBoundsException ex) {
			throw new IllegalBankAccessException("EPC bits not available");
		}
	}
}
