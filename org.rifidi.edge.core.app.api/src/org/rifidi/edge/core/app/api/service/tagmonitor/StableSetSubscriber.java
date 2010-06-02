/**
 * 
 */
package org.rifidi.edge.core.app.api.service.tagmonitor;

import java.util.Set;

import org.rifidi.edge.core.app.api.service.RifidiAppSubscriber;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

/**
 * @author kyle
 *
 */
public interface StableSetSubscriber extends RifidiAppSubscriber {
	void stableSetReached(Set<TagReadEvent> stableSet);

}
