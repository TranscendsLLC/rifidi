/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread.groups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.rifidi.edge.core.messages.DatacontainerEvent;
import org.rifidi.edge.epcglobal.aleread.filters.FieldMatcher;
import org.rifidi.edge.epcglobal.aleread.filters.RangeFieldMatcher;

/**
 * @author jochen
 * 
 */
public class EPCTAGGROUPPatternMatcher implements GroupMatcher {

	private static Pattern matchrange = Pattern.compile("\\[(\\d*)-(\\d*)\\]");
	private Pattern pattern;
	private Map<Integer, FieldMatcher> matchers;
	private static Pattern pattern_tag = Pattern
			.compile("urn:epc:pat:([a-zA-Z0-9\\-]+):(((\\d+|\\[\\d+\\-\\d+\\]|\\*|X)\\.)*(\\d+|X|\\*|\\[\\d+\\-\\d+\\]))$");
	private Map<String, List<DatacontainerEvent>> groupsToTags;
	private String name_base;

	public EPCTAGGROUPPatternMatcher(String input) {
		groupsToTags = new HashMap<String, List<DatacontainerEvent>>();
		matchers = new HashMap<Integer, FieldMatcher>();

		Matcher mat = pattern_tag.matcher(input);
		if (mat.find()) {
			StringBuilder patternBuilder = new StringBuilder("urn:epc:tag:");
			StringBuilder nameBuilder = new StringBuilder("urn:epc:tag:");
			patternBuilder.append(mat.group(1));
			patternBuilder.append(":");
			nameBuilder.append(mat.group(1));
			nameBuilder.append(":");
			String[] split = mat.group(2).split("\\.");
			int count = 0;
			for (String stuff : split) {
				mat = matchrange.matcher(stuff);
				if (mat.find()) {
					matchers.put(count + 1, new RangeFieldMatcher(Long
							.parseLong(mat.group(1)), Long.parseLong(mat
							.group(2))));
					patternBuilder.append("(\\d+)");
					nameBuilder.append("<>");
					count++;
				} else if (stuff.equals("X")) {
					patternBuilder.append("(\\d+)");
					nameBuilder.append("<>");
					count++;
				} else if (stuff.equals("*")) {
					patternBuilder.append("\\d+");
					nameBuilder.append("*");
				} else {
					patternBuilder.append(stuff);
					nameBuilder.append(stuff);
				}
				patternBuilder.append("\\.");
				nameBuilder.append(".");
			}
			patternBuilder.deleteCharAt(patternBuilder.length() - 1);
			patternBuilder.deleteCharAt(patternBuilder.length() - 1);
			patternBuilder.append("$");
			System.out.println(patternBuilder.toString());
			pattern = Pattern.compile(patternBuilder.toString());
			nameBuilder.deleteCharAt(nameBuilder.length() - 1);
			name_base = nameBuilder.toString();
		}
	}

	@Override
	public Map<String, List<DatacontainerEvent>> getGrouped() {
		Map<String, List<DatacontainerEvent>> ret = new HashMap<String, List<DatacontainerEvent>>(
				groupsToTags);
		groupsToTags.clear();
		return ret;
	}

	@Override
	public boolean match(String input, DatacontainerEvent event) {
		Matcher match = pattern.matcher(input);
		if (!match.find()) {
			return false;
		}
		String groupname = name_base;
		for (int count = 1; count <= match.groupCount(); count++) {
			// check if a regular matcher exists for the group
			if (matchers.get(count) != null) {
				// match it
				if (!matchers.get(count).match(match.group(count))) {
					return false;
				} else {
					// create the groupname
					groupname = groupname
							.replaceFirst("<>", match.group(count));
				}
			}
			// should be an X-field
			else {
				groupname = groupname.replaceFirst("<>", match.group(count));
			}
		}
		if (!groupsToTags.containsKey(groupname)) {
			groupsToTags.put(groupname, new ArrayList<DatacontainerEvent>());
		}
		groupsToTags.get(groupname).add(event);
		return true;
	}
}
