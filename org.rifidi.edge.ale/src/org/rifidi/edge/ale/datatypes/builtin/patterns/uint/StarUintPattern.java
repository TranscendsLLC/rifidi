/*
 *  StarUintPattern.java
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
 * This class is the '*' pattern for the Unsigned Integer pattern, as described
 * in 6.2.2.3 of the ALE 1.1 specification. It matches any value.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class StarUintPattern extends UintPattern {

	/**
	 * The regex pattern for this uint pattern
	 */
	protected static String PATTERN = "\\*";

	/**
	 * Protected constructor so that a new object can only be made from the
	 * factory in the super class
	 */
	protected StarUintPattern() {

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
		return true;
	}

}
