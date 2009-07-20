/**
 * 
 */
package org.rifidi.edge.core.sensors.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.management.AttributeList;

import org.rifidi.configuration.Configuration;
import org.rifidi.configuration.RifidiService;
import org.rifidi.edge.api.rmi.dto.ReaderDTO;
import org.rifidi.edge.api.rmi.dto.SessionDTO;
import org.rifidi.edge.core.sensors.CompositeSensor;
import org.rifidi.edge.core.sensors.Sensor;
import org.rifidi.edge.core.sensors.SensorSession;
import org.rifidi.edge.core.sensors.SensorUpdate;
import org.rifidi.edge.core.sensors.exceptions.DuplicateSubscriptionException;
import org.rifidi.edge.core.sensors.exceptions.ImmutableException;
import org.rifidi.edge.core.sensors.exceptions.InUseException;
import org.rifidi.edge.core.sensors.exceptions.NotSubscribedException;
import org.rifidi.edge.core.services.notification.data.ReadCycle;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

/**
 * A reader creates and manages instances of sessions. The reader itself holds
 * all configuration parameters and creates the sessions according to these. The
 * returned sensorSession objects are immutable and if some parameters of the
 * factory change after a session has been created, the created session will
 * retain its configuration until it is destroyed and a new one is created
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public abstract class AbstractSensor<T extends SensorSession> extends
		RifidiService implements SensorUpdate, CompositeSensor {
	/** Sensors connected to this connectedSensors. */
	protected final Set<Sensor> receivers = new CopyOnWriteArraySet<Sensor>();

	/**
	 * Receivers are objects that need to gather tag reads. The tag reads are
	 * stored in a queue.
	 */
	protected final Map<Object, LinkedBlockingQueue<ReadCycle>> subscriberToQueueMap = new ConcurrentHashMap<Object, LinkedBlockingQueue<ReadCycle>>();

	/**
	 * Create a new reader session. If there are no more sessions available null
	 * is returned.
	 * 
	 * @return
	 */
	abstract public SensorSession createReaderSession();

	/**
	 * Get all currently created reader sessions. The Key is the ID of the
	 * session, and the value is the actual session
	 * 
	 * @return
	 */
	abstract public Map<String, SensorSession> getSensorSessions();

	/**
	 * Destroy a sesnor session.
	 * 
	 * @param session
	 */
	abstract public void destroySensorSession(SensorSession session);

	/**
	 * Send properties that have been modified to the physical reader
	 */
	abstract public void applyPropertyChanges();

	/** True if the sensor is currently in use. */
	protected AtomicBoolean inUse = new AtomicBoolean(false);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.PollableSensor#receive(java.lang.Object)
	 */
	@Override
	public ReadCycle receive(final Object receiver)
			throws NotSubscribedException {
		LinkedBlockingQueue<ReadCycle> queue = subscriberToQueueMap
				.get(receiver);
		if (queue != null) {
			synchronized (queue) {
				Set<ReadCycle> rcs = new HashSet<ReadCycle>();
				queue.drainTo(rcs);
				long time = System.currentTimeMillis();
				Set<TagReadEvent> tagReads = new HashSet<TagReadEvent>();
				for (ReadCycle cycle : rcs) {
					for(TagReadEvent event:cycle.getTags()){
						tagReads.add(event);	
					}
				}
				ReadCycle cycle = new ReadCycle(tagReads, getName(), time);
				return cycle;
			}
		}
		throw new NotSubscribedException(receiver + " is not subscribed.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.SensorUpdate#subscribe(java.lang.Object)
	 */
	@Override
	public void subscribe(final Object receiver)
			throws DuplicateSubscriptionException {
		if (subscriberToQueueMap.containsKey(receiver)) {
			throw new DuplicateSubscriptionException(receiver
					+ " is already subscribed.");
		}
		subscriberToQueueMap.put(receiver, new LinkedBlockingQueue<ReadCycle>());
		inUse.compareAndSet(false, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.Sensor#getName()
	 */
	@Override
	public String getName() {
		return getID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.SensorUpdate#unsubscribe(java.lang.Object)
	 */
	@Override
	public synchronized void unsubscribe(final Object receiver)
			throws NotSubscribedException {
		if (!subscriberToQueueMap.containsKey(receiver)) {
			throw new NotSubscribedException(receiver + " is not subscribed.");
		}
		subscriberToQueueMap.remove(receiver);
		if (subscriberToQueueMap.isEmpty() && receivers.isEmpty()) {
			inUse.compareAndSet(true, false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.SensorUpdate#removeReceiver(org.rifidi.edge
	 * .core.sensors.Sensor)
	 */
	@Override
	public void removeReceiver(final Sensor receiver) {
		if (subscriberToQueueMap.isEmpty() && receivers.isEmpty()) {
			inUse.compareAndSet(true, false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.SensorUpdate#addReceiver(org.rifidi.edge
	 * .core.sensors.Sensor)
	 */
	@Override
	public void addReceiver(final Sensor receiver) {
		receivers.add(receiver);
		inUse.compareAndSet(false, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.SensorUpdate#setName(java.lang.String)
	 */
	@Override
	public void setName(final String name) throws ImmutableException,
			InUseException {
		// TODO: should be possible when we merged the readers with the logical
		// readers
		throw new ImmutableException(getName() + " is immutable.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.PollableSensor#send(org.rifidi.edge.core
	 * .services.notification.data.ReadCycle)
	 */
	@Override
	public void send(final ReadCycle cycle) {
		for (Sensor receiver : receivers) {
			receiver.send(cycle);
		}
		for (LinkedBlockingQueue<ReadCycle> queue : subscriberToQueueMap
				.values()) {
			queue.add(cycle);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.Sensor#isImmutable()
	 */
	@Override
	public Boolean isImmutable() {
		return true;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.CompositeSensor#getChildren()
	 */
	@Override
	public Set<String> getChildren() {
		return Collections.emptySet();
	}

	/***
	 * This method returns the Data Transfer Object for this Reader
	 * 
	 * @param config
	 *            The Configuration Object for this AbstractSensor
	 * @return A data transfer object for this reader
	 */
	public ReaderDTO getDTO(final Configuration config) {
		String readerID = config.getServiceID();
		String factoryID = config.getFactoryID();
		AttributeList attrs = config.getAttributes(config.getAttributeNames());
		List<SessionDTO> sessionDTOs = new ArrayList<SessionDTO>();
		for (SensorSession s : this.getSensorSessions().values()) {
			sessionDTOs.add(s.getDTO());
		}
		ReaderDTO dto = new ReaderDTO(readerID, factoryID, attrs, sessionDTOs);
		return dto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.configuration.RifidiService#destroy()
	 */
	@Override
	public void destroy() {
		unregister();
		receivers.clear();
	}

}
