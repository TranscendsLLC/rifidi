/**
 * 
 */
package org.rifidi.edge.core.sensors.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.rifidi.edge.core.sensors.CompositeUpdateableSensor;
import org.rifidi.edge.core.sensors.PollableSensor;
import org.rifidi.edge.core.sensors.Sensor;
import org.rifidi.edge.core.sensors.UpdateableSensor;
import org.rifidi.edge.core.sensors.exceptions.DuplicateSubscriptionException;
import org.rifidi.edge.core.sensors.exceptions.ImmutableException;
import org.rifidi.edge.core.sensors.exceptions.InUseException;
import org.rifidi.edge.core.sensors.exceptions.NotSubscribedException;
import org.rifidi.edge.core.services.notification.data.ReadCycle;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class SensorImpl implements Sensor, CompositeUpdateableSensor {
	private volatile String name;
	/** Once a reader is immutable it can never be changed back. */
	private final Boolean immutable;
	/** True if the sensor is currently in use. */
	private AtomicBoolean inUse;
	/** Children of this node. */
	private final Set<UpdateableSensor> childNodes;
	/** Map with the subscriber as key and it's queue as value. */
	public final Map<Object, LinkedBlockingQueue<ReadCycle>> subscriberToQueueMap;
	/** Set containing all nodes that are connected as receivers. */
	private final Set<PollableSensor> receivers;

	/**
	 * @param name
	 * @param childNodes
	 * @param immutable
	 */
	public SensorImpl(final String name,
			final Collection<UpdateableSensor> childNodes,
			final Boolean immutable) {
		this.name = name;
		this.childNodes = new CopyOnWriteArraySet<UpdateableSensor>();
		this.childNodes.addAll(childNodes);
		this.immutable = immutable;
		this.subscriberToQueueMap = new ConcurrentHashMap<Object, LinkedBlockingQueue<ReadCycle>>();
		this.receivers = new CopyOnWriteArraySet<PollableSensor>();
		this.inUse = new AtomicBoolean(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wtf.Sensor#getName()
	 */
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.UpdateableSensor#setName(java.lang.String)
	 */
	public void setName(String name) throws ImmutableException, InUseException {
		if (isInUse()) {
			throw new InUseException(getName() + " is currently in use.");
		}
		if (isImmutable()) {
			throw new ImmutableException(getName() + " is immutable.");
		}
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wtf.Sensor#getChildren()
	 */
	public Set<String> getChildren() {
		Set<String> ret = new HashSet<String>();
		for (UpdateableSensor sensor : childNodes) {
			ret.add(sensor.getName());
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.UpdateableSensor#addChild(wtf.impl.SensorImpl
	 * )
	 */
	public void addChild(final UpdateableSensor child)
			throws ImmutableException, InUseException {
		if (isImmutable()) {
			throw new ImmutableException(getName() + " is immutable.");
		}
		if (isInUse()) {
			throw new InUseException(getName() + " is in use.");
		}
		childNodes.add(child);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.CompositeUpdateableSensor#removeChild(org
	 * .rifidi.edge.core.sensors.UpdateableSensor)
	 */
	@Override
	public void removeChild(UpdateableSensor child) throws ImmutableException,
			InUseException {
		if (isImmutable()) {
			throw new ImmutableException(getName() + " is immutable.");
		}
		if (isInUse()) {
			throw new InUseException(getName() + " is in use.");
		}
		childNodes.remove(child);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.UpdateableSensor#removeChildren(java.util
	 * .Collection)
	 */
	public void removeChildren(final Collection<UpdateableSensor> children)
			throws ImmutableException, InUseException {
		if (isImmutable()) {
			throw new ImmutableException(getName() + " is immutable.");
		}
		if (isInUse()) {
			throw new InUseException(getName() + " is in use.");
		}
		childNodes.removeAll(children);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.UpdateableSensor#subscribe(java.lang.Object)
	 */
	public void subscribe(Object object) throws DuplicateSubscriptionException {
		if (subscriberToQueueMap.containsKey(object)) {
			throw new DuplicateSubscriptionException(object
					+ " is already subscribed.");
		}
		subscriberToQueueMap.put(object, null);
		inUse.compareAndSet(false, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.UpdateableSensor#unsubscribe(java.lang.Object
	 * )
	 */
	public void unsubscribe(Object object) throws NotSubscribedException {
		if (!subscriberToQueueMap.containsKey(object)) {
			throw new NotSubscribedException(object + " is not subscribed.");
		}
		subscriberToQueueMap.remove(object);
		if (subscriberToQueueMap.isEmpty() && receivers.isEmpty()) {
			inUse.compareAndSet(true, false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.UpdateableSensor#addReceiver(wtf.impl.SensorImpl
	 * )
	 */
	public void addReceiver(PollableSensor receiver) {
		receivers.add(receiver);
		inUse.compareAndSet(false, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.UpdateableSensor#removeReceiver(wtf.Sensor)
	 */
	public void removeReceiver(PollableSensor receiver) {
		if (subscriberToQueueMap.isEmpty() && receivers.isEmpty()) {
			inUse.compareAndSet(true, false);
		}
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.Sensor#isImmutable()
	 */
	@Override
	public Boolean isImmutable() {
		return immutable;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.Sensor#isInUse()
	 */
	@Override
	public Boolean isInUse() {
		return inUse.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.PollableSensor#send(org.rifidi.edge.core
	 * .services.notification.data.ReadCycle)
	 */
	@Override
	public void send(ReadCycle cycle) {
		for (PollableSensor receiver : receivers) {
			receiver.send(cycle);

		}
		for (LinkedBlockingQueue<ReadCycle> queue : subscriberToQueueMap
				.values()) {
			queue.add(cycle);
		}
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.PollableSensor#receive(java.lang.Object)
	 */
	public Set<ReadCycle> receive(Object object) throws NotSubscribedException {
		LinkedBlockingQueue<ReadCycle> queue = subscriberToQueueMap.get(object);
		if (queue != null) {
			synchronized (queue) {
				Set<ReadCycle> ret = new HashSet<ReadCycle>();
				queue.drainTo(ret);
				return ret;
			}
		}
		throw new NotSubscribedException(object + " is not subscribed.");
	}	
}
