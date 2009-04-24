package org.rifidi.edge.epcglobal.aleread.filters;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractPatternMatcher implements PatternMatcher{

	protected static Pattern matchrange = Pattern.compile("\\[(\\d*)-(\\d*)\\]");
	protected List<FieldMatcher> matchers;
	protected Pattern pattern;

	@Override
	public boolean match(String matchee) {
		Matcher match = pattern.matcher(matchee);
		if (!match.find()) {
			return false;
		}
		for (int count = 1; count <= match.groupCount(); count++) {
			if (!matchers.get(count - 1).match(match.group(count))) {
				return false;
			}
		}
		return true;
	} 
}