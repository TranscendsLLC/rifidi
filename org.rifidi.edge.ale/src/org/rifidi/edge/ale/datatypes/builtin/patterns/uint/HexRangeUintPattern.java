/*
 *  HexRangeUintPattern.java
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
 * This is the Hex Range Unsigned Integer pattern as described by section
 * 6.2.2.3 in the ALE 1.1 specification. It returns true if the uint is
 * <code>lo<=uint<=hi</code>. The original format of the uint does not matter,
 * only its value.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class HexRangeUintPattern extends UintPattern {

	/**
	 * The regex for this uint pattern
	 */
	protected static String PATTERN = "\\[" + HexUintPattern.PATTERN + "-"
			+ HexUintPattern.PATTERN + "\\]";

	/**
	 * The low integer
	 */
	private BigInteger lo;

	/**
	 * the high integer
	 */
	private BigInteger hi;

	/**
	 * The pattern is in the format '[x123-x456]'
	 * 
	 * @param pattern
	 *            two hex numbers that represent the lo and the hi values to
	 *            match against
	 * @throws IllegalArgumentException
	 *             if lo>hi
	 */
	protected HexRangeUintPattern(String pattern) {
		// split into hi and low
		String[] pieces = pattern.split("-");

		// get rid of '[' character at beginning
		String loNum = pieces[0].replaceFirst("\\[", "");

		// get rid of ']' character at end
		String hiNum = pieces[1].replaceFirst("\\]", "");

		// parse lo num, removing 'x' character
		lo = new BigInteger(loNum.substring(1), 16);

		// parse hi num, removing 'x' character
		hi = new BigInteger(hiNum.substring(1), 16);
		if (lo.compareTo(hi) > 0) {
			throw new IllegalArgumentException(
					"lo is greater than hi in uint pattern " + pattern);
		}
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
		if (lo.compareTo(bigInteger) <= 0) {
			if (hi.compareTo(bigInteger) >= 0) {
				return true;
			}
		}
		return false;
	}

}
