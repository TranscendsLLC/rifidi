/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread.filters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * matcher for a UINT decimal range.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class UINTDECIMALPatternMatcher implements PatternMatcher {
	/** Pattern to collect the fields. */
	private static Pattern range = Pattern.compile("^\\[(\\d+)-(\\d+)\\]$");
	/** True if pattern matches always*/
	private boolean always = false;
	/** Raw pattern. */
	private String match = null;
	/** Lowest value allowed. */
	private Long lo;
	/** Highest value allowed. */
	private Long hi;

	/**
	 * Constructor.
	 * 
	 * @param input
	 */
	public UINTDECIMALPatternMatcher(String input) {
		if (input.equals("*")) {
			always = true;
			return;
		}
		Matcher mat = range.matcher(input);
		if (mat.find()) {
			lo = Long.parseLong(mat.group(1));
			hi = Long.parseLong(mat.group(2));
			return;
		}
		match = input;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.pflanzenmoerder.pattern.PatternMatcher#match(java.lang.String)
	 */
	@Override
	public boolean match(String matchee) {
		if (always) {
			return true;
		}
		if (matchee.equals(match)) {
			return true;
		}
		// strip the leading x
		Long val = Long.parseLong(matchee);
		return val >= lo && val <= hi ? true : false;
	}
}
