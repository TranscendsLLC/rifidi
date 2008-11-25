/*
 *  UintPattern.java
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
import java.util.regex.Pattern;

/**
 * @author kyle
 * 
 */
public abstract class UintPattern {

	private static Pattern HEX_UINT_PAT = Pattern
			.compile(HexUintPattern.PATTERN);
	private static Pattern STAR_UINT_PAT = Pattern
			.compile(StarUintPattern.PATTERN);
	private static Pattern HEX_RANGE_UINT_PAT = Pattern
			.compile(HexRangeUintPattern.PATTERN);
	private static Pattern HEX_MASK_UINT_PAT = Pattern
			.compile(HexMaskUintPattern.PATTERN);
	private static Pattern DEC_UINT_PAT = Pattern
			.compile(DecimalUintPattern.PATTERN);
	private static Pattern DEC_RANGE_UINT_PAT = Pattern
			.compile(DecimalRangeUintPattern.PATTERN);

	public static UintPattern create(String pattern) {
		if (HEX_UINT_PAT.matcher(pattern).matches()) {
			return new HexUintPattern(pattern);
		} else if (STAR_UINT_PAT.matcher(pattern).matches()) {
			return new StarUintPattern();
		} else if (HEX_RANGE_UINT_PAT.matcher(pattern).matches()) {
			return new HexRangeUintPattern(pattern);
		} else if (HEX_MASK_UINT_PAT.matcher(pattern).matches()) {
			return new HexMaskUintPattern(pattern);
		} else if (DEC_UINT_PAT.matcher(pattern).matches()) {
			return new DecimalUintPattern(pattern);
		} else if (DEC_RANGE_UINT_PAT.matcher(pattern).matches()) {
			return new DecimalRangeUintPattern(pattern);
		} else {
			throw new IllegalArgumentException(pattern
					+ " is not a valid uint Pattern");
		}

	}

	public abstract boolean matches(BigInteger bigInteger);

}
