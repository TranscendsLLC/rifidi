/*
 *  C1G2UserBank.java
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

/**
 * This class models the User Memory Bank (bank 3) on a Gen2 tag
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class C1G2UserBank extends MemoryBank {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new User Memory Bank
	 * 
	 * @param bits
	 *             The bits of the memory bank as a binary string composed only
	 *            of '1' and '0' characters. The leftmost bit is at position 0
	 */
	public C1G2UserBank(String bits) {
		super.setMemoryBank(bits);
	}
}
