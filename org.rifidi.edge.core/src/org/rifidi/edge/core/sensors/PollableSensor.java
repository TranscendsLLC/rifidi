package org.rifidi.edge.core.sensors;

import java.util.Collection;
import java.util.Set;

import org.rifidi.edge.core.sensors.exceptions.NotSubscribedException;
import org.rifidi.edge.core.services.notification.data.ReadCycle;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

public interface PollableSensor {

	/**
	 * Get the name of the sensor.
	 * 
	 * @return
	 */
	String getName();

	/**
	 * Objects subscribed to the sensor use this method to acquire the read
	 * results.
	 * 
	 * @param reads
	 */
	Set<ReadCycle> receive(Object object) throws NotSubscribedException;

	/**
	 * Send sensor results to this sensor.
	 * 
	 * @param cycle
	 */
	public void send(ReadCycle cycle);

}