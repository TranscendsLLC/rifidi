/*
 *  FixedAddress.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.ale.fields.generic;

/**
 * This class wraps the information necessary to read from a fixed address on a
 * tag
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class FixedAddress {

	private int _bank;
	private int _length;
	private int _offset;

	/**
	 * 
	 * @param bank
	 *            Implementation dependent.
	 * @param length
	 *            the number of contiguous bits to read
	 * @param offset
	 *            Implementation dependent
	 */
	public FixedAddress(int bank, int length, int offset) {
		_bank = bank;
		_length = length;
		_offset = offset;
	}

	/**
	 * @return the bank
	 */
	public int get_bank() {
		return _bank;
	}

	/**
	 * @return the length
	 */
	public int get_length() {
		return _length;
	}

	/**
	 * @return the offset
	 */
	public int get_offset() {
		return _offset;
	}

}
