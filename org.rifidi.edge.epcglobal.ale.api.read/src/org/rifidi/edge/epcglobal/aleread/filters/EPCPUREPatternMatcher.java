/*
 * 
 * EPCPUREPatternMatcher.java
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
package org.rifidi.edge.epcglobal.aleread.filters;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Identifies an epc pure encoding. urn:epc:id:number.number.number
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class EPCPUREPatternMatcher extends AbstractPatternMatcher {
	private static Pattern pattern_pure = Pattern
			.compile("urn:epc:idpat:([a-zA-Z0-9\\-]+):((\\d+|\\*|\\[\\d+\\-\\d+\\])\\.(\\d+|\\*|\\[\\d+\\-\\d+\\])\\.(\\d+|\\*|\\[\\d+\\-\\d+\\]))$");

	public static boolean isValidPattern(String pattern) {
		return pattern_pure.matcher(pattern).groupCount() == 2;
	}

	public EPCPUREPatternMatcher(String input) {
		matchers = new ArrayList<FieldMatcher>();

		Matcher mat = pattern_pure.matcher(input);
		StringBuilder patternBuilder = new StringBuilder("urn:epc:id:");
		patternBuilder.append(mat.group(1));
		patternBuilder.append(":");
		String[] split = mat.group(2).split("\\.");
		boolean onlyStars = false;
		for (String stuff : split) {
			mat = matchrange.matcher(stuff);
			if (stuff.equals("*")) {
				patternBuilder.append("\\d*");
				onlyStars = true;
			} else {
				if (onlyStars) {
					throw new RuntimeException("invalid, idiot!");
				}
				patternBuilder.append(stuff);
			}
			patternBuilder.append(".");
		}
		patternBuilder.deleteCharAt(patternBuilder.length() - 1);
		patternBuilder.append("$");
		pattern = Pattern.compile(patternBuilder.toString());
	}

}
