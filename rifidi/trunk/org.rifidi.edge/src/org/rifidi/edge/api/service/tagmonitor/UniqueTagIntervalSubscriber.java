/**
 * 
 */
package org.rifidi.edge.api.service.tagmonitor;

import org.rifidi.edge.api.service.RifidiAppSubscriber;
import org.rifidi.edge.notification.TagReadEvent;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 *
 */
public interface UniqueTagIntervalSubscriber extends RifidiAppSubscriber{
	
	void tagSeen(TagReadEvent tag);

}
