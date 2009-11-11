/*
 * 
 * AbstractSensor.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
/**
 * 
 */
package org.rifidi.edge.core.sensors.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.management.AttributeList;

import org.osgi.framework.BundleContext;
import org.rifidi.edge.api.rmi.dto.ReaderDTO;
import org.rifidi.edge.api.rmi.dto.SessionDTO;
import org.rifidi.edge.core.configuration.Configuration;
import org.rifidi.edge.core.configuration.ConfigurationType;
import org.rifidi.edge.core.configuration.services.RifidiService;
import org.rifidi.edge.core.exceptions.CannotCreateSessionException;
import org.rifidi.edge.core.sensors.CompositeSensor;
import org.rifidi.edge.core.sensors.Sensor;
import org.rifidi.edge.core.sensors.SensorSession;
import org.rifidi.edge.core.sensors.SensorUpdate;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.sensors.exceptions.CannotDestroySensorException;
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
	/** True if the sensor is currently in use. */
	protected AtomicBoolean inUse = new AtomicBoolean(false);

	/**
	 * This constructor is only for CGLIB. DO NOT OVERWRITE!
	 */
	public AbstractSensor() {
		super();
	}

	/**
	 * Receivers are objects that need to gather tag reads. The tag reads are
	 * stored in a queue.
	 */
	protected final Map<Object, LinkedBlockingQueue<ReadCycle>> subscriberToQueueMap = new ConcurrentHashMap<Object, LinkedBlockingQueue<ReadCycle>>();

	/**
	 * Create a new sensor session.
	 * 
	 * @return id of the created session
	 * @exception CannotCreateSessionException
	 *                - if the session cannot be created
	 */
	abstract public String createSensorSession()
			throws CannotCreateSessionException;

	/**
	 * This method is called when a sensor session is being created from a DTO,
	 * such as restoring the session from persistance.
	 * 
	 * @param sessionDTO
	 * @return the ID of the session
	 * @exception CannotCreateSessionException
	 *                if the session cannot be created
	 */
	abstract public String createSensorSession(SessionDTO sessionDTO)
			throws CannotCreateSessionException;


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
	abstract public void destroySensorSession(String id)
			throws CannotDestroySensorException;

	/**
	 * Send properties that have been modified to the physical reader
	 */
	abstract public void applyPropertyChanges();

	/**
	 * Notifier the sensor that a command configuration has disappeared.
	 * 
	 * @param commandConfiguration
	 * @param properties
	 */
	abstract public void unbindCommandConfiguration(
			AbstractCommandConfiguration<?> commandConfiguration,
			Map<?, ?> properties);

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
					for (TagReadEvent event : cycle.getTags()) {
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
		subscriberToQueueMap
				.put(receiver, new LinkedBlockingQueue<ReadCycle>());
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

	/**
	 * Register the reader to OSGi.
	 * 
	 * @param context
	 * @param readerType
	 */
	public void register(BundleContext context, String readerType) {
		Map<String, String> parms = new HashMap<String, String>();
		parms.put("type", ConfigurationType.READER.toString());
		parms.put("reader", readerType);
		Set<String> interfaces = new HashSet<String>();
		interfaces.add(AbstractSensor.class.getName());
		register(context, interfaces, parms);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.configuration.RifidiService#destroy()
	 */
	@Override
	protected void destroy() {
		unregister();
		receivers.clear();
	}

}
