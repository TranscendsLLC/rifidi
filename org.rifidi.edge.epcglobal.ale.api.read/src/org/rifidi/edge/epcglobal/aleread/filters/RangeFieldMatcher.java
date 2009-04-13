/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread.filters;

/**
 * @author jochen
 * 
 */
public class RangeFieldMatcher implements FieldMatcher {

	private Long lo, hi;

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
