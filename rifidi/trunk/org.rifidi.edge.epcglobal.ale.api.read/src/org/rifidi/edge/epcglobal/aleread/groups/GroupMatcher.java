/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread.groups;

import java.util.List;
import java.util.Map;

import org.rifidi.edge.core.services.notification.data.DatacontainerEvent;

/**
 * @author jochen
 * 
 */
public interface GroupMatcher {
	/**
	 * Try to put the given tag into the group. Returns true on success.
	 * 
	 * @param matchees
	 */
	public boolean match(String input, DatacontainerEvent event);

	/**
	 * Get a map containing the group name as key and a list of tags as value.
	 * 
	 * @return
	 */
	public Map<String, List<DatacontainerEvent>> getGrouped();
}
