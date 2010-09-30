/**
 * 
 */
package org.rifidi.edge.core.app.api.service.tagmonitor;

import java.util.Set;

import org.rifidi.edge.core.app.api.service.RifidiAppSubscriber;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface StableSetSubscriber extends RifidiAppSubscriber {

	/**
	 * This method returns the set of tags that were seen during a stable set
	 * operation.
	 * 
	 * @param stableSet
	 */
	void stableSetReached(Set<TagReadEvent> stableSet);

}
