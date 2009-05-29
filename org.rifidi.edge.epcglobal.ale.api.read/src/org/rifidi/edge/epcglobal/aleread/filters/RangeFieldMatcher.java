/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread.filters;

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
