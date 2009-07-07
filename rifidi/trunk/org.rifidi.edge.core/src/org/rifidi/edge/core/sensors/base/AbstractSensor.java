/**
 * 
 */
package org.rifidi.edge.core.sensors.base;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.management.AttributeList;

import org.rifidi.configuration.Configuration;
import org.rifidi.configuration.RifidiService;
import org.rifidi.edge.api.rmi.dto.ReaderDTO;
import org.rifidi.edge.api.rmi.dto.SessionDTO;
import org.rifidi.edge.core.sensors.PollableSensor;
import org.rifidi.edge.core.sensors.SensorSession;
import org.rifidi.edge.core.sensors.UpdateableSensor;
import org.rifidi.edge.core.sensors.exceptions.DuplicateSubscriptionException;
import org.rifidi.edge.core.sensors.exceptions.ImmutableException;
import org.rifidi.edge.core.sensors.exceptions.InUseException;
import org.rifidi.edge.core.sensors.exceptions.NotSubscribedException;
import org.rifidi.edge.core.services.notification.data.ReadCycle;

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
		RifidiService implements UpdateableSensor {
	/** Sensors connected to this connectedSensors. */
	protected Set<PollableSensor> receivers;

	/**
	 * Receivers are objects that need to gather tag reads. The tag reads are
	 * stored in a queue.
	 */
	protected Map<Object, LinkedBlockingQueue<ReadCycle>> subscriberToQueueMap;

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
	abstract public Map<String, SensorSession> getReaderSessions();

	/**
	 * Destroy a reader session.
	 * 
	 * @param session
	 */
	abstract public void destroyReaderSession(SensorSession session);

	/**
	 * Send properties that have been modified to the physical reader
	 */
	abstract public void applyPropertyChanges();

	/** True if the sensor is currently in use. */
	protected AtomicBoolean inUse;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.PollableSensor#receive(java.lang.Object)
	 */
	@Override
	public Set<ReadCycle> receive(Object receiver)
			throws NotSubscribedException {
		Set<ReadCycle> ret = new HashSet<ReadCycle>();
		subscriberToQueueMap.get(receiver).drainTo(ret);
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.PollableSensor#getName()
	 */
	@Override
	public String getName() {
		return getID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.UpdateableSensor#subscribe(java.lang.Object)
	 */
	@Override
	public void subscribe(Object receiver)
			throws DuplicateSubscriptionException {
		if (subscriberToQueueMap.containsKey(receiver)) {
			throw new DuplicateSubscriptionException(receiver
					+ " is already subscribed.");
		}
		subscriberToQueueMap.put(receiver, null);
		inUse.compareAndSet(false, true);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.UpdateableSensor#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) throws ImmutableException, InUseException {
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
	public void send(ReadCycle cycle) {
		for (PollableSensor receiver : receivers) {
			receiver.send(cycle);
		}
		for (LinkedBlockingQueue<ReadCycle> queue : subscriberToQueueMap
				.values()) {
			queue.add(cycle);
		}
	}

	/***
	 * This method returns the Data Transfer Object for this Reader
	 * 
	 * @param config
	 *            The Configuration Object for this AbstractSensor
	 * @return A data transfer object for this reader
	 */
	public ReaderDTO getDTO(Configuration config) {
		String readerID = config.getServiceID();
		String factoryID = config.getFactoryID();
		AttributeList attrs = config.getAttributes(config.getAttributeNames());
		List<SessionDTO> sessionDTOs = new ArrayList<SessionDTO>();
		for (SensorSession s : this.getReaderSessions().values()) {
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
		receivers.clear();
	}

}
