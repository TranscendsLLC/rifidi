/*
 * 
 * AbstractPatternMatcher.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
package org.rifidi.edge.epcglobal.aleread.filters;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract base class for matchers. Provides a basic range matcher for values
 * like [10-20]
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public abstract class AbstractPatternMatcher implements PatternMatcher {

	protected static Pattern matchrange = Pattern
			.compile("\\[(\\d*)-(\\d*)\\]");
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