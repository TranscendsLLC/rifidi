/**
 * 
 */
package org.rifidi.edge.core.app.api.service.tagmonitor;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author kyle
 * 
 */
public interface StableSetService {

	public void subscribe(StableSetSubscriber subscriber, List<ReadZone> zones,
			Float stableSetTime, TimeUnit stableSetTimeUnit, boolean unique);
	
	public void unsubscribe(StableSetSubscriber subscriber);

}
