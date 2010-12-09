/*
 * 
 * RangeFieldMatcher.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
/**
 * 
 */
package org.rifidi.edge.ale.read.filters;

/**
 * Matches a field against a number range.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class RangeFieldMatcher implements FieldMatcher {
	/** Low and high value of the range. */
	private Long lo, hi;

	/**
	 * Constructor.
	 * 
	 * @param lo
	 * @param hi
	 */
	public RangeFieldMatcher(Long lo, Long hi) {
		this.lo = lo;
		this.hi = hi;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.pflanzenmoerder.pattern.FieldMatcher#match(java.lang.String)
	 */
	@Override
	public boolean match(String field) {
		Long val = Long.parseLong(field);
		if (lo <= val && hi >= val) {
			return true;
		}
		return false;
	}

}
