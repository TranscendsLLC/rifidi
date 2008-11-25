/*
 *  HexUintPattern.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.ale.datatypes.builtin.patterns.uint;

import java.math.BigInteger;

/**
 * This class is the Hex Unsigned Integer Pattern as described in 6.2.2.3 of the
 * ALE 1.1 specification. It matches an unsigned integer with the same value as
 * the one given in the pattern (the original format (i.e. 'hex' or 'decimal' of
 * the uint does not matter -- only the value).
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class HexUintPattern extends UintPattern {

	/**
	 * The regex pattern for this UintPattern
	 */
	protected static String PATTERN = "x[0-9 a-f A-F]+";

	/**
	 * The number represented in the pattern
	 */
	BigInteger _pattern;

	/**
	 * Protected constructor so that a new object can only be made from the
	 * factory in the super class
	 * 
	 * @param pattern
	 *            the pattern to match the uint against
	 */
	protected HexUintPattern(String pattern) {
		_pattern = new BigInteger(pattern.substring(1), 16);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.ale.datatypes.builtin.patterns.uint.UintPattern#matches
	 * (java.math.BigInteger)
	 */
	@Override
	public boolean matches(BigInteger bigInteger) {
		if (null == bigInteger) {
			return false;
		}
		return _pattern.equals(bigInteger);
	}

}
