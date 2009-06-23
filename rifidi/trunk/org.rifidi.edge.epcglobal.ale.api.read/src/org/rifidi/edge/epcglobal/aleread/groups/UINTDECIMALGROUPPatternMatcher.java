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

import org.rifidi.edge.core.services.notification.data.DatacontainerEvent;

/**
 * Group matcher for UINT DECIMAL patterns. See ALE 1.1 6.2.2.3.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class UINTDECIMALGROUPPatternMatcher implements GroupMatcher {
	private static Pattern range = Pattern.compile("^\\[(\\d*)-(\\d*)\\]$");
	private boolean always = false;
	private String match = null;
	private Long lo;
	private Long hi;
	private Map<String, List<DatacontainerEvent>> groupsToTags;
	private boolean distinct = true;

	/**
	 * Constructor.
	 * 
	 * @param input
	 */
	public UINTDECIMALGROUPPatternMatcher(String input) {
		groupsToTags = new HashMap<String, List<DatacontainerEvent>>();
		if (input.equals("*")) {
			always = true;
			return;
		}
		if (input.equals("X")) {
			distinct = true;
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
	 * @see
	 * org.rifidi.edge.epcglobal.aleread.groups.GroupMatcher#match(java.lang
	 * .String, org.rifidi.edge.core.messages.DatacontainerEvent)
	 */
	@Override
	public boolean match(String input, DatacontainerEvent event) {
		if (always) {
			addToGroup("always", event);
			return true;
		}
		if (input.equals(match)) {
			addToGroup(match, event);
			return true;
		}
		if (distinct) {
			addToGroup(input, event);
			return true;
		}
		Long val = Long.parseLong(input);
		if (val >= lo && val <= hi) {
			addToGroup(input, event);
			return true;
		}
		return false;
	}

	/**
	 * Add the given value to the key.
	 * 
	 * @param key
	 * @param value
	 */
	private void addToGroup(String key, DatacontainerEvent value) {
		if (!groupsToTags.containsKey(key)) {
			groupsToTags.put(key, new ArrayList<DatacontainerEvent>());
		}
		groupsToTags.get(key).add(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.epcglobal.aleread.groups.GroupMatcher#getGrouped()
	 */
	@Override
	public Map<String, List<DatacontainerEvent>> getGrouped() {
		Map<String, List<DatacontainerEvent>> ret = new HashMap<String, List<DatacontainerEvent>>(
				groupsToTags);
		groupsToTags.clear();
		return ret;
	}

}
