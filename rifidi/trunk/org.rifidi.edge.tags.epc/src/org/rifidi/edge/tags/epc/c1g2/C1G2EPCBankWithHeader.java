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
		super.setMemoryBank(bits);
	}
}
