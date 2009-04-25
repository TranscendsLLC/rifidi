/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread.filters;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jochen
 * 
 */
public class UINTHEXPatternMatcher implements PatternMatcher {
	private static Pattern hexRange = Pattern
			.compile("^\\[x([A-D0-9]+)-x([A-D0-9]+)\\]$");
	private static Pattern andComp = Pattern
		.compile("^&x([A-D0-9]+)=x([A-D0-9]+)$");
	private boolean always = false;
	private String match = null;
	private Long lo;
	private Long hi;
	private BigInteger mask;
	private BigInteger compare;
	
	public UINTHEXPatternMatcher(String input) {
		if (input.equals("*")) {
			always = true;
			return;
		}
		Matcher mat = hexRange.matcher(input);
		if (mat.find()) {
			lo = Long.parseLong(mat.group(1), 16);
			hi = Long.parseLong(mat.group(2), 16);
			return;
		}
		mat = andComp.matcher(input);
		if(mat.find()){
			mask=new BigInteger(mat.group(1),16);
			compare=new BigInteger(mat.group(2),16);
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
		if(mask!=null){
			BigInteger input=new BigInteger(matchee, 16);
			return mask.and(input).equals(compare);
		}
		//strip the leading x
		Long val = Long.parseLong(matchee.substring(1), 16);
		return val >= lo && val <= hi ? true : false;
	}

}
