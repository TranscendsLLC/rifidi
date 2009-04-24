/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread.filters;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jochen
 * 
 */
public class EPCPUREPatternMatcher extends AbstractPatternMatcher {
	private static Pattern pattern_pure = Pattern
			.compile("urn:epc:idpat:(.*):(.*\\..*$)");

	public static boolean isValidPattern(String pattern){
		return pattern_pure.matcher(pattern).groupCount()==2;
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
