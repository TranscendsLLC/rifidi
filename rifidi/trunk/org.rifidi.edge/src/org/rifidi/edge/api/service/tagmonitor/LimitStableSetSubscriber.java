/**
 * 
 */
package org.rifidi.edge.api.service.tagmonitor;

import java.util.Set;

import org.rifidi.edge.api.service.RifidiAppSubscriber;
import org.rifidi.edge.notification.TagReadEvent;

/**
 * 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public interface LimitStableSetSubscriber extends RifidiAppSubscriber {
	/**
	 * This method returns the set of tags that were seen during a stable set
	 * operation.
	 * 
	 * @param stableSet
	 */
	void stableSetReached(Set<TagReadEvent> stableSet);
}
