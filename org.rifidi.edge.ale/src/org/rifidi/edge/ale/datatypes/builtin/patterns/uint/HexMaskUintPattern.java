/*
 *  HexMaskUintPattern.java
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
 * This is the Hex Mask Unsigned Integer pattern as described by section 6.2.2.3
 * in the ALE 1.1 specification. It has the form of &mask=compare. It returns
 * true if the uint is equal to the compare number after being ANDed with the
 * mask. The original format of the uint does not matter, only its value.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class HexMaskUintPattern extends UintPattern {

	/**
	 * The regex for this uint pattern
	 */
	protected static String PATTERN = "&" + HexUintPattern.PATTERN + "="
			+ HexUintPattern.PATTERN;

	private BigInteger _mask;
	private BigInteger _compare;

	/**
	 * the format for this pattern is '&x123=x456', where the x123 is the mask,
	 * and the x456 is the compare
	 * 
	 * @param pattern
	 */
	protected HexMaskUintPattern(String pattern) {
		// split pattern into mask and compare
		String[] pieces = pattern.split("=");

		// get rid of the '&' character
		String maskString = pieces[0].replaceFirst("&", "");

		// make a new big integer, removing the 'x' character
		_mask = new BigInteger(maskString.substring(1), 16);

		// make a new big integer, removing the 'x character
		_compare = new BigInteger(pieces[1].substring(1), 16);
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
		BigInteger maskedInt = bigInteger.and(_mask);
		if (maskedInt.compareTo(_compare) == 0) {
			return true;
		}
		return false;
	}

}
