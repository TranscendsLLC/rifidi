/**
 * 
 */
package org.rifidi.edge.core.sensors.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;

import org.rifidi.edge.core.sensors.LogicalSensor;
import org.rifidi.edge.core.sensors.PhysicalSensor;
import org.rifidi.edge.core.sensors.exceptions.DuplicateSubscriptionException;
import org.rifidi.edge.core.sensors.exceptions.NotSubscribedException;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class LogicalSensorImpl implements LogicalSensor {
	/** Sensors connected to this connectedSensors. */
	private Set<LogicalSensorImpl> connectedSensors;
	/** Sensors contained in this connectedSensors. */
	private Set<LogicalSensorImpl> containedSensors;
	/**
	 * Receivers are objects that need to gather tag reads. The tag reads are
	 * stored in a queue.
	 */
	private Map<Object, LinkedBlockingQueue<Set<TagReadEvent>>> receiversToQueues;

	/**
	 * Constructor.
	 */
	public LogicalSensorImpl() {
		connectedSensors = new CopyOnWriteArraySet<LogicalSensorImpl>();
		containedSensors = new CopyOnWriteArraySet<LogicalSensorImpl>();
		receiversToQueues = new ConcurrentHashMap<Object, LinkedBlockingQueue<Set<TagReadEvent>>>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.LogicalSensor#send(java.util.Set)
	 */
	@Override
	public void send(Set<TagReadEvent> tagReads) {
		for (LinkedBlockingQueue<Set<TagReadEvent>> queue : receiversToQueues
				.values()) {
			queue.add(tagReads);
		}
		for (LogicalSensor sensor : connectedSensors) {
			sensor.send(tagReads);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.PhysicalSensor#receive(java.lang.Object)
	 */
	@Override
	public Set<TagReadEvent> receive(Object receiver) {
		Set<TagReadEvent> ret = new HashSet<TagReadEvent>();
		Set<Set<TagReadEvent>> process = new HashSet<Set<TagReadEvent>>();
		receiversToQueues.get(receiver).drainTo(process);
		for (Set<TagReadEvent> tags : receiversToQueues.get(receiver)) {
			ret.addAll(tags);
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.PhysicalSensor#subscribe(java.lang.Object)
	 */
	@Override
	public synchronized void subscribe(Object receiver)
			throws DuplicateSubscriptionException {
		if (!receiversToQueues.containsKey(receiver)) {
			receiversToQueues.put(receiver,
					new LinkedBlockingQueue<Set<TagReadEvent>>());
			return;
		}
		throw new DuplicateSubscriptionException(receiver
				+ " is already subscribed to " + this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.PhysicalSensor#unsubscribe(java.lang.Object)
	 */
	@Override
	public synchronized void unsubscribe(Object receiver)
			throws NotSubscribedException {
		if (receiversToQueues.containsKey(receiver)) {
			receiversToQueues.remove(receiver);
		}
		throw new NotSubscribedException(receiver + " is not subscribed to "
				+ this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.LogicalSensor#addSensor(org.rifidi.edge.
	 * core.sensors.LogicalSensorImpl)
	 */
	@Override
	public void addSensor(LogicalSensorImpl sensor) {
		containedSensors.add(sensor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.LogicalSensor#removeSensor(org.rifidi.edge
	 * .core.sensors.PhysicalSensor)
	 */
	@Override
	public void removeSensor(PhysicalSensor sensor) {
		containedSensors.remove(sensor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.LogicalSensor#getContainedSensors()
	 */
	@Override
	public Set<LogicalSensorImpl> getContainedSensors() {
		Set<LogicalSensorImpl> ret = new HashSet<LogicalSensorImpl>();
		ret.addAll(containedSensors);
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.PhysicalSensor#connectSensor(org.rifidi.
	 * edge.core.sensors.LogicalSensorImpl)
	 */
	@Override
	public void connectSensor(LogicalSensorImpl sensor) {
		connectedSensors.add(sensor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.PhysicalSensor#disconnectSensor(org.rifidi
	 * .edge.core.sensors.PhysicalSensor)
	 */
	@Override
	public void disconnectSensor(PhysicalSensor sensor) {
		connectedSensors.remove(sensor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.PhysicalSensor#getConnectedSensors()
	 */
	@Override
	public Set<LogicalSensorImpl> getConnectedSensors() {
		Set<LogicalSensorImpl> ret = new HashSet<LogicalSensorImpl>();
		ret.addAll(connectedSensors);
		return ret;
	}

}
