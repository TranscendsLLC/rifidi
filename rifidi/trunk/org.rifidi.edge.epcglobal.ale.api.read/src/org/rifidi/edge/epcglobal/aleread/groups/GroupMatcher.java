/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread.groups;

import java.util.List;

import org.rifidi.edge.core.messages.DatacontainerEvent;

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
	 * Get an ordered list of tags that are in this group. This list will also
	 * contain subgroups.
	 * 
	 * @return
	 */
	public List<DatacontainerEvent> getGrouped();
}
