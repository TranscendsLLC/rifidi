/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
/**
 * 
 */
package org.rifidi.edge.sensors;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.rifidi.edge.notification.ReadCycle;
import org.rifidi.edge.notification.TagReadEvent;
import org.rifidi.edge.services.EsperEventContainer;

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
	public final Map<Object, LinkedBlockingQueue<ReadCycle>> tagSubscriberToQueueMap;
	/** Set containing all nodes that are connected as receivers. */
	private final Set<Sensor> receivers;
	/**
	 * This queue is just like the tag subscriber queue, except that it stores
	 * all events which are not Tag Read Events.
	 */
	protected final Map<Object, LinkedBlockingQueue<Object>> eventSubscriberToQueueMap;

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
		this.tagSubscriberToQueueMap = new ConcurrentHashMap<Object, LinkedBlockingQueue<ReadCycle>>();
		eventSubscriberToQueueMap = new ConcurrentHashMap<Object, LinkedBlockingQueue<Object>>();
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
	 * @see org.rifidi.edge.sensors.SensorUpdate#setName(java.lang.String)
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
	 * org.rifidi.edge.sensors.SensorUpdate#addChild(wtf.impl.SensorImpl )
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
	 * @see org.rifidi.edge.sensors.CompositeSensorUpdate#removeChild(org
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
	 * @see org.rifidi.edge.sensors.SensorUpdate#removeChildren(java.util
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
	 * org.rifidi.edge.sensors.SensorUpdate#subscribe(java.lang.Object)
	 */
	public void subscribe(Object object) throws DuplicateSubscriptionException {
		if (tagSubscriberToQueueMap.containsKey(object)) {
			throw new DuplicateSubscriptionException(object
					+ " is already subscribed.");
		}
		tagSubscriberToQueueMap.put(object,
				new LinkedBlockingQueue<ReadCycle>());
		eventSubscriberToQueueMap
				.put(object, new LinkedBlockingQueue<Object>());
		inUse.compareAndSet(false, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.SensorUpdate#unsubscribe(java.lang.Object )
	 */
	public void unsubscribe(Object object) throws NotSubscribedException {
		if (!tagSubscriberToQueueMap.containsKey(object)) {
			throw new NotSubscribedException(object + " is not subscribed.");
		}
		tagSubscriberToQueueMap.remove(object);
		eventSubscriberToQueueMap.remove(object);
		if (tagSubscriberToQueueMap.isEmpty() && receivers.isEmpty()) {
			inUse.compareAndSet(true, false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.SensorUpdate#addReceiver(wtf.impl.SensorImpl
	 * )
	 */
	public void addReceiver(Sensor receiver) {
		receivers.add(receiver);
		inUse.compareAndSet(false, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.SensorUpdate#removeReceiver(org.rifidi.edge
	 * .core.sensors.Sensor)
	 */
	@Override
	public void removeReceiver(Sensor receiver) {
		if (tagSubscriberToQueueMap.isEmpty() && receivers.isEmpty()) {
			inUse.compareAndSet(true, false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.Sensor#isImmutable()
	 */
	@Override
	public Boolean isImmutable() {
		return immutable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.Sensor#isInUse()
	 */
	@Override
	public Boolean isInUse() {
		return inUse.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.Sensor#send(org.rifidi.edge.core.services
	 * .notification.data.ReadCycle)
	 */
	@Override
	public void send(ReadCycle cycle) {
		for (Sensor receiver : receivers) {
			receiver.send(cycle);

		}
		for (LinkedBlockingQueue<ReadCycle> queue : tagSubscriberToQueueMap
				.values()) {
			queue.add(cycle);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.Sensor#sendEvent(java.lang.Object)
	 */
	@Override
	public void sendEvent(Object event) {
		for (Sensor receiver : receivers) {
			receiver.sendEvent(event);
		}
		for (LinkedBlockingQueue<Object> queue : eventSubscriberToQueueMap
				.values()) {
			queue.add(event);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.Sensor#receive(java.lang.Object)
	 */
	@Override
	public EsperEventContainer receive(Object object)
			throws NotSubscribedException {
		LinkedBlockingQueue<ReadCycle> tagQueue = tagSubscriberToQueueMap
				.get(object);
		LinkedBlockingQueue<Object> eventQueue = eventSubscriberToQueueMap
				.get(object);
		if (tagQueue == null || eventQueue == null) {
			throw new NotSubscribedException(object + " is not subscribed.");
		}

		Set<ReadCycle> rcs = new HashSet<ReadCycle>();
		synchronized (tagQueue) {
			tagQueue.drainTo(rcs);
		}

		Set<Object> events = new HashSet<Object>();
		synchronized (eventQueue) {
			eventQueue.drainTo(events);
		}

		long time = System.currentTimeMillis();
		Set<TagReadEvent> tagReads = new HashSet<TagReadEvent>();
		for (ReadCycle cycle : rcs) {
			for (TagReadEvent event : cycle.getTags()) {
				tagReads.add(event);
			}
		}
		ReadCycle cycle = new ReadCycle(tagReads, getName(), time);
		EsperEventContainer eventContainer = new EsperEventContainer();
		eventContainer.setReadCycle(cycle);
		eventContainer.setOtherEvents(events);
		return eventContainer;
	}

}
