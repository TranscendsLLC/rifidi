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

import java.io.IOException;
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
import org.rifidi.edge.api.CommandDTO;
import org.rifidi.edge.api.ReaderDTO;
import org.rifidi.edge.api.SessionDTO;
import org.rifidi.edge.configuration.Configuration;
import org.rifidi.edge.configuration.ConfigurationType;
import org.rifidi.edge.configuration.RifidiService;
import org.rifidi.edge.exceptions.CannotCreateSessionException;
import org.rifidi.edge.notification.NotifierService;
import org.rifidi.edge.notification.ReadCycle;
import org.rifidi.edge.notification.TagReadEvent;
import org.rifidi.edge.services.EsperEventContainer;

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
public abstract class AbstractSensor<T extends SensorSession> extends RifidiService
		implements SensorUpdate, CompositeSensor {
	/** Sensors connected to this connectedSensors. */
	protected final Set<Sensor> receivers = new CopyOnWriteArraySet<Sensor>();
	/** True if the sensor is currently in use. */
	protected AtomicBoolean inUse = new AtomicBoolean(false);
	/** The notifier Service */
	protected NotifierService notifierService;

	/**
	 * This constructor is only for CGLIB. DO NOT OVERWRITE!
	 */
	public AbstractSensor() {
		super();
	}

	/**
	 * Receivers are objects that need to gather tag reads. The tag reads are stored
	 * in a queue.
	 */
	protected final Map<Object, LinkedBlockingQueue<ReadCycle>> tagSubscriberToQueueMap = new ConcurrentHashMap<Object, LinkedBlockingQueue<ReadCycle>>();

	/**
	 * This queue is just like the tag subscriber queue, except that it stores all
	 * events which are not Tag Read Events.
	 */
	protected final Map<Object, LinkedBlockingQueue<Object>> eventSubscriberToQueueMap = new ConcurrentHashMap<Object, LinkedBlockingQueue<Object>>();

	/**
	 * Create a new sensor session.
	 * 
	 * @return id of the created session
	 * @exception CannotCreateSessionException - if the session cannot be created
	 */
	abstract public String createSensorSession() throws CannotCreateSessionException;

	/**
	 * This method is called when a sensor session is being created from a DTO, such
	 * as restoring the session from persistance.
	 * 
	 * @param sessionDTO
	 * @return the ID of the session
	 * @exception CannotCreateSessionException if the session cannot be created
	 */
	abstract public String createSensorSession(SessionDTO sessionDTO) throws CannotCreateSessionException;

	/**
	 * Get all currently created reader sessions. The Key is the ID of the session,
	 * and the value is the actual session
	 * 
	 * @return
	 */
	abstract public Map<String, SensorSession> getSensorSessions();

	/**
	 * Destroy a sesnor session.
	 * 
	 * @param session
	 */
	abstract public void destroySensorSession(String id) throws CannotDestroySensorException;

	/**
	 * Send properties that have been modified to the physical reader
	 */
	abstract public void applyPropertyChanges();

	/**
	 * This method returns a display name for clients to use. This way readers can
	 * have user-friendly names (such as "Dock Door") in a client.
	 * 
	 * @return The display name of the Sensor
	 */
	abstract protected String getDisplayName();

	/**
	 * Notifier the sensor that a command configuration has disappeared.
	 * 
	 * @param commandConfiguration
	 * @param properties
	 */
	abstract public void unbindCommandConfiguration(AbstractCommandConfiguration<?> commandConfiguration,
			Map<?, ?> properties);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.PollableSensor#receive(java.lang.Object)
	 */
	@Override
	public EsperEventContainer receive(final Object receiver) throws NotSubscribedException {
		LinkedBlockingQueue<ReadCycle> tagQueue = tagSubscriberToQueueMap.get(receiver);
		LinkedBlockingQueue<Object> eventQueue = eventSubscriberToQueueMap.get(receiver);
		if (tagQueue == null || eventQueue == null) {
			throw new NotSubscribedException(receiver + " is not subscribed.");
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.SensorUpdate#subscribe(java.lang.Object)
	 */
	@Override
	public void subscribe(final Object receiver) throws DuplicateSubscriptionException {
		if (tagSubscriberToQueueMap.containsKey(receiver)) {
			throw new DuplicateSubscriptionException(receiver + " is already subscribed.");
		}
		tagSubscriberToQueueMap.put(receiver, new LinkedBlockingQueue<ReadCycle>());
		eventSubscriberToQueueMap.put(receiver, new LinkedBlockingQueue<Object>());
		inUse.compareAndSet(false, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.Sensor#getName()
	 */
	@Override
	public String getName() {
		return getID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.SensorUpdate#unsubscribe(java.lang.Object)
	 */
	@Override
	public synchronized void unsubscribe(final Object receiver) throws NotSubscribedException {
		if (!tagSubscriberToQueueMap.containsKey(receiver)) {
			throw new NotSubscribedException(receiver + " is not subscribed.");
		}
		tagSubscriberToQueueMap.remove(receiver);
		eventSubscriberToQueueMap.remove(receiver);
		if (tagSubscriberToQueueMap.isEmpty() && receivers.isEmpty()) {
			inUse.compareAndSet(true, false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.SensorUpdate#removeReceiver(org.rifidi.edge
	 * .core.sensors.Sensor)
	 */
	@Override
	public void removeReceiver(final Sensor receiver) {
		receivers.remove(receiver);
		if (tagSubscriberToQueueMap.isEmpty() && receivers.isEmpty()) {
			inUse.compareAndSet(true, false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.SensorUpdate#addReceiver(org.rifidi.edge
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
	 * @see org.rifidi.edge.sensors.SensorUpdate#setName(java.lang.String)
	 */
	@Override
	public void setName(final String name) throws ImmutableException, InUseException {
		// TODO: should be possible when we merged the readers with the logical
		// readers
		throw new ImmutableException(getName() + " is immutable.");
	}

	/***
	 * Set the wrapper for the Notify Service.
	 * 
	 * @param wrapper The JMS Notifier to set
	 */
	public void setNotifiyService(NotifierService notifierService) {
		this.notifierService = notifierService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.PollableSensor#send(org.rifidi.edge.core
	 * .services.notification.data.ReadCycle)
	 */
	@Override
	public void send(final ReadCycle cycle) {
		for (Sensor receiver : receivers) {
			receiver.send(cycle);
		}
		for (LinkedBlockingQueue<ReadCycle> queue : tagSubscriberToQueueMap.values()) {
			queue.add(cycle);
		}
		String shouldNotify = System.getProperty("org.rifidi.ui.notify");
		if (notifierService != null) {
			if (shouldNotify == null || shouldNotify.equalsIgnoreCase("true")) {
				notifierService.tagSeen(cycle);
			}
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
		for (LinkedBlockingQueue<Object> queue : eventSubscriberToQueueMap.values()) {
			queue.add(event);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.Sensor#isImmutable()
	 */
	@Override
	public Boolean isImmutable() {
		return true;
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
	 * @see org.rifidi.edge.sensors.CompositeSensor#getChildren()
	 */
	@Override
	public Set<String> getChildren() {
		return Collections.emptySet();
	}

	/***
	 * This method returns the Data Transfer Object for this Reader
	 * 
	 * @param config The Configuration Object for this AbstractSensor
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
		ReaderDTO dto = new ReaderDTO(readerID, factoryID, attrs, sessionDTOs, getDisplayName());
		return dto;
	}

	/**
	 * Register the reader to OSGi.
	 * 
	 * Registers the service with the following params:
	 * 
	 * <pre>
	 * type - "reader"
	 * reader - the reader type supplied as an argument
	 * serviceid - the service ID of the reader
	 * </pre>
	 * 
	 * @param context    The Bundlecontext to use
	 * @param readerType The Type of reader to register it as
	 */
	public void register(BundleContext context, String readerType) {
		register(context, readerType, new HashMap<String, String>());
	}

	/**
	 * Register the reader to OSGi.
	 * 
	 * Registers the service with the following params:
	 * 
	 * <pre>
	 * type - "reader"
	 * reader - the reader type supplied as an argument
	 * serviceid - the service ID of the reader
	 * </pre>
	 * 
	 * @param context      The Bundlecontext to use
	 * @param readerType   The Type of reader to register it as
	 * @param filterParams Any additional OSGi filter params to use when registering
	 *                     the service
	 */
	public void register(BundleContext context, String readerType, Map<String, String> filterParams) {
		Map<String, String> parms = new HashMap<String, String>();
		parms.put("type", ConfigurationType.READER.toString());
		parms.put("reader", readerType);
		parms.put("serviceid", getID());
		if (filterParams != null) {
			parms.putAll(filterParams);
		}
		Set<String> interfaces = new HashSet<String>();
		interfaces.add(AbstractSensor.class.getName());
		register(context, interfaces, parms);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.configuration.RifidiService#destroy()
	 */
	@Override
	protected void destroy() {
		unregister();
		receivers.clear();
	}

	/**
	 * Resets the sensor session
	 * 
	 * @throws CannotDestroySensorException
	 * @throws CannotCreateSessionException 
	 * @throws IOException 
	 */
	public void resetSensor() throws CannotDestroySensorException, CannotCreateSessionException, IOException {
		// If there are no sessions, just return.  
		if (!this.getSensorSessions().keySet().isEmpty()) {
			//If there is a session, delete and recreate it along with recreating any commands
			String sessionID = this.getSensorSessions().keySet().iterator().next();
			
			//Get the list of commands from the current session
			List<CommandDTO> commands = this.getSensorSessions().get(sessionID).getCommands();
			//Destroy the current session
			this.destroySensorSession(sessionID);
			
			//Create a new session
			String newSessionID = this.createSensorSession();
			SensorSession newSession = this.getSensorSessions().get(newSessionID);
			//Resubmit all the commands
			for(CommandDTO command:commands) {
				newSession.submit(command.getCommandID(), command.getInterval(), command.getTimeUnit());
			}
			newSession.connect();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Sensor: " + getID();
	}
}
