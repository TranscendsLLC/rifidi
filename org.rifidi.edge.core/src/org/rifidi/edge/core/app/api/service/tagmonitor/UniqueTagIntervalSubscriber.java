/**
 * 
 */
package org.rifidi.edge.core.app.api.service.tagmonitor;

import org.rifidi.edge.core.app.api.service.RifidiAppSubscriber;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 *
 */
public interface UniqueTagIntervalSubscriber extends RifidiAppSubscriber{
	
	void tagSeen(TagReadEvent tag);

}
