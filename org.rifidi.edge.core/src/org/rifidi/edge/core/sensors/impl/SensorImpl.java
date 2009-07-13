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

import org.rifidi.edge.core.sensors.CompositeSensorUpdate;
import org.rifidi.edge.core.sensors.Sensor;
import org.rifidi.edge.core.sensors.SensorUpdate;
import org.rifidi.edge.core.sensors.exceptions.DuplicateSubscriptionException;
import org.rifidi.edge.core.sensors.exceptions.ImmutableException;
import org.rifidi.edge.core.sensors.exceptions.InUseException;
import org.rifidi.edge.core.sensors.exceptions.NotSubscribedException;
import org.rifidi.edge.core.services.notification.data.ReadCycle;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class SensorImpl implements SensorUpdate, CompositeSensorUpdate {
	private volatile String name;
	/** Once a reader is immutable it can never be changed back. */
	private final Boolean immutable;
	/** True if the sensor is currently in use. */
	private AtomicBoolean inUse;
	/** Children of this node. */
	private final Set<SensorUpdate> childNodes;
	/** Map with the subscriber as key and it's queue as value. */
	public final Map<Object, LinkedBlockingQueue<ReadCycle>> subscriberToQueueMap;
	/** Set containing all nodes that are connected as receivers. */
	private final Set<Sensor> receivers;

	/**
	 * @param name
	 * @param childNodes
	 * @param immutable
	 */
	public SensorImpl(final String name,
			final Collection<SensorUpdate> childNodes, final Boolean immutable) {
		this.name = name;
		this.childNodes = new CopyOnWriteArraySet<SensorUpdate>();
		this.childNodes.addAll(childNodes);
		this.immutable = immutable;
		this.subscriberToQueueMap = new ConcurrentHashMap<Object, LinkedBlockingQueue<ReadCycle>>();
		this.receivers = new CopyOnWriteArraySet<Sensor>();
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
	 * @see org.rifidi.edge.core.sensors.SensorUpdate#setName(java.lang.String)
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
		for (SensorUpdate sensor : childNodes) {
			ret.add(sensor.getName());
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.SensorUpdate#addChild(wtf.impl.SensorImpl )
	 */
	public void addChild(final SensorUpdate child) throws ImmutableException,
			InUseException {
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
	 * @see org.rifidi.edge.core.sensors.CompositeSensorUpdate#removeChild(org
	 * .rifidi.edge.core.sensors.UpdateableSensor)
	 */
	@Override
	public void removeChild(SensorUpdate child) throws ImmutableException,
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
	 * @see org.rifidi.edge.core.sensors.SensorUpdate#removeChildren(java.util
	 * .Collection)
	 */
	public void removeChildren(final Collection<SensorUpdate> children)
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
	 * org.rifidi.edge.core.sensors.SensorUpdate#subscribe(java.lang.Object)
	 */
	public void subscribe(Object object) throws DuplicateSubscriptionException {
		if (subscriberToQueueMap.containsKey(object)) {
			throw new DuplicateSubscriptionException(object
					+ " is already subscribed.");
		}
		subscriberToQueueMap.put(object, new LinkedBlockingQueue<ReadCycle>());
		inUse.compareAndSet(false, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.SensorUpdate#unsubscribe(java.lang.Object )
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
	 * org.rifidi.edge.core.sensors.SensorUpdate#addReceiver(wtf.impl.SensorImpl
	 * )
	 */
	public void addReceiver(Sensor receiver) {
		receivers.add(receiver);
		inUse.compareAndSet(false, true);
	}


	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.SensorUpdate#removeReceiver(org.rifidi.edge.core.sensors.Sensor)
	 */
	@Override
	public void removeReceiver(Sensor receiver) {
		if (subscriberToQueueMap.isEmpty() && receivers.isEmpty()) {
			inUse.compareAndSet(true, false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.Sensor#isImmutable()
	 */
	@Override
	public Boolean isImmutable() {
		return immutable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.Sensor#isInUse()
	 */
	@Override
	public Boolean isInUse() {
		return inUse.get();
	}


	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.Sensor#send(org.rifidi.edge.core.services.notification.data.ReadCycle)
	 */
	@Override
	public void send(ReadCycle cycle) {
		for (Sensor receiver : receivers) {
			receiver.send(cycle);

		}
		for (LinkedBlockingQueue<ReadCycle> queue : subscriberToQueueMap
				.values()) {
			queue.add(cycle);
		}
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.Sensor#receive(java.lang.Object)
	 */
	@Override
	public ReadCycle receive(Object object) throws NotSubscribedException {
		LinkedBlockingQueue<ReadCycle> queue = subscriberToQueueMap.get(object);
		if (queue != null) {
			synchronized (queue) {
				Set<ReadCycle> rcs = new HashSet<ReadCycle>();
				queue.drainTo(rcs);
				long time=System.currentTimeMillis();
				Set<TagReadEvent> tagReads=new HashSet<TagReadEvent>();
				for(ReadCycle cycle:rcs){
					for(TagReadEvent event:cycle.getTags()){
						tagReads.add(event);	
					}
				}
				ReadCycle cycle=new ReadCycle(tagReads,getName(),time);
				return cycle;
			}
		}
		throw new NotSubscribedException(object + " is not subscribed.");
	}

}
