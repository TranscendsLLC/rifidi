/*
 * 
 * EPCTAGPatternMatcher.java
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

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Dientifies a tag pattern: urn:epc:pat:tagtypeidentifier:*
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class EPCTAGPatternMatcher extends AbstractPatternMatcher implements
		PatternMatcher {
	private static Pattern pattern_tag = Pattern
			.compile("urn:epc:pat:([a-zA-Z0-9\\-]+):(((\\d+|\\[\\d+\\-\\d+\\]|\\*)\\.)*(\\d+|\\*|\\[\\d+\\-\\d+\\]))$");

	public static boolean isValidPattern(String pattern) {
		return pattern_tag.matcher(pattern).find();
	}

	public EPCTAGPatternMatcher(String input) {
		matchers = new ArrayList<FieldMatcher>();

		Matcher mat = pattern_tag.matcher(input);
		mat.find();
		StringBuilder patternBuilder = new StringBuilder("urn:epc:tag:");
		patternBuilder.append(mat.group(1));
		patternBuilder.append(":");
		String[] split = mat.group(2).split("\\.");
		for (String stuff : split) {
			mat = matchrange.matcher(stuff);
			if (mat.find()) {
				matchers.add(new RangeFieldMatcher(
						Long.parseLong(mat.group(1)), Long.parseLong(mat
								.group(2))));
				patternBuilder.append("(\\d*)");

			} else if (stuff.equals("*")) {
				patternBuilder.append("\\d*");
			} else {
				patternBuilder.append(stuff);
			}
			patternBuilder.append(".");
		}
		patternBuilder.deleteCharAt(patternBuilder.length() - 1);
		patternBuilder.append("$");
		pattern = Pattern.compile(patternBuilder.toString());
	}

}
