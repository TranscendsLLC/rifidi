package org.rifidi.edge.core.sensors;


import org.rifidi.edge.core.sensors.exceptions.DuplicateSubscriptionException;
import org.rifidi.edge.core.sensors.exceptions.ImmutableException;
import org.rifidi.edge.core.sensors.exceptions.InUseException;
import org.rifidi.edge.core.sensors.exceptions.NotSubscribedException;
/**
 * 
 * @author Jochen Mader - jochen@pramari.com
 *
 */
public interface UpdateableSensor extends PollableSensor {

	/**
	 * 
	 * @param name
	 * @throws ImmutableException
	 * @throws InUseException
	 */
	void setName(String name) throws ImmutableException, InUseException;

	/**
	 * Subscribe an object to the sensor.
	 * 
	 * @param object
	 * @throws DuplicateSubscriptionException
	 */
	void subscribe(Object object) throws DuplicateSubscriptionException;

	/**
	 * Unsubscribe an object from the sensor.
	 * 
	 * @param object
	 * @throws NotSubscribedException
	 */
	void unsubscribe(Object object) throws NotSubscribedException;

	/**
	 * Add a sensor to the list of sensors that receive sensor reads from this
	 * sensor.
	 * 
	 * @param receiver
	 */
	void addReceiver(PollableSensor receiver);

	/**
	 * Remove a sensor from the list of sensors that receive sensor reads from
	 * this sensor.
	 * 
	 * @param receiver
	 */
	void removeReceiver(PollableSensor receiver);

}