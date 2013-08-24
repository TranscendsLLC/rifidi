/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
/**
 * 
 */
package org.rifidi.edge.adapter.alien.autonomous;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.management.MBeanInfo;

import org.rifidi.edge.api.CommandDTO;
import org.rifidi.edge.api.SessionDTO;
import org.rifidi.edge.configuration.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.configuration.JMXMBean;
import org.rifidi.edge.configuration.Property;
import org.rifidi.edge.configuration.PropertyType;
import org.rifidi.edge.notification.NotifierService;
import org.rifidi.edge.sensors.AbstractCommandConfiguration;
import org.rifidi.edge.sensors.AbstractSensor;
import org.rifidi.edge.sensors.SensorSession;

/**
 * A special sensor that is used to passively receive notifications from Alien
 * readers that are in autonomous mode. It cannot send commands to the Alien
 * reader
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
@JMXMBean
public class AlienAutonomousSensor extends
		AbstractSensor<AlienAutonomousSensorSession> {

	/** The session for this reader */
	private volatile AtomicReference<AlienAutonomousSensorSession> session = new AtomicReference<AlienAutonomousSensorSession>();
	/** Flag to check if this reader is destroied. */
	private AtomicBoolean destroyed = new AtomicBoolean(false);
	/** The FACTORY_ID of the session */
	private AtomicInteger sessionID = new AtomicInteger(0);
	/** The port of the server socket */
	private volatile Integer notifyPort = 54321;
	/** Maximum number of autonomous readers supported concurrently */
	private volatile Integer maxNumberAutonomousReaders = 15;
	/** Provided by spring. */
	private final Set<AbstractCommandConfiguration<?>> commands;
	private String displayName = "Alien Autonomous";
	/** Mbeaninfo for this class. */
	public static final MBeanInfo mbeaninfo;
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy.getMBeanInfo(AlienAutonomousSensor.class);
	}

	/**
	 * Constructor.
	 */
	public AlienAutonomousSensor(Set<AbstractCommandConfiguration<?>> commands) {
		super();
		this.commands = commands;
	}


	/**
	 * @param notifierService
	 *            the notifierService to set
	 */
	public void setNotifierService(NotifierService notifierService) {
		this.notifierService = notifierService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.base.AbstractSensor#applyPropertyChanges()
	 */
	@Override
	public void applyPropertyChanges() {
		// Nothing to do here.

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.base.AbstractSensor#createReaderSession(
	 * org.rifidi.edge.api.rmi.dto.SessionDTO)
	 */
	@Override
	public String createSensorSession(SessionDTO sessionDTO) {
		if (session.get() == null) {
			Integer id = sessionID.incrementAndGet();
			if (session.compareAndSet(null, new AlienAutonomousSensorSession(
					this, Integer.toString(id), notifierService,
					notifyPort, maxNumberAutonomousReaders, commands))) {
				if (sessionDTO.getCommands() != null) {
					for (CommandDTO command : sessionDTO.getCommands()) {
						session.get().submit(command.getCommandID(),
								command.getInterval(), command.getTimeUnit());
					}
				}
				notifierService.addSessionEvent(getID(), id.toString());
				return id.toString();
			}
		}
		AlienAutonomousSensorSession autosession = session.get();
		if (autosession != null) {
			return autosession.getID();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.base.AbstractSensor#createSensorSession()
	 */
	@Override
	public String createSensorSession() {
		if (session.get() == null) {
			Integer id = sessionID.incrementAndGet();
			if (session.compareAndSet(null, new AlienAutonomousSensorSession(
					this, Integer.toString(id), notifierService,
					notifyPort, maxNumberAutonomousReaders, commands))) {
				notifierService.addSessionEvent(getID(), id.toString());
				return id.toString();
			}
		}
		AlienAutonomousSensorSession autosession = session.get();
		if (autosession != null) {
			return autosession.getID();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.base.AbstractSensor#destroySensorSession
	 * (java.lang.String)
	 */
	@Override
	public void destroySensorSession(String id) {
		AlienAutonomousSensorSession autosession = session.get();
		if (autosession.getID().equals(id)) {
			if (session.compareAndSet(autosession, null)) {
				autosession.disconnect();
				notifierService
						.removeSessionEvent(getID(), autosession.getID());
				this.session = null;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.base.AbstractSensor#unbindCommandConfiguration
	 * (org.rifidi.edge.sensors.commands.AbstractCommandConfiguration,
	 * java.util.Map)
	 */
	@Override
	public void unbindCommandConfiguration(
			AbstractCommandConfiguration<?> commandConfiguration,
			Map<?, ?> properties) {
		AlienAutonomousSensorSession autosession = session.get();
		if (autosession != null) {
			autosession.suspendCommand(commandConfiguration.getID());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.base.AbstractSensor#getReaderSessions()
	 */
	@Override
	public Map<String, SensorSession> getSensorSessions() {
		Map<String, SensorSession> map = new HashMap<String, SensorSession>();
		AlienAutonomousSensorSession autosession = session.get();
		if (autosession != null) {
			map.put(autosession.getID(), autosession);
		}
		return map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.configuration.RifidiService#destroy()
	 */
	@Override
	protected void destroy() {
		if (destroyed.compareAndSet(false, true)) {
			super.destroy();
			AlienAutonomousSensorSession aliensession = session.get();
			if (aliensession != null) {
				destroySensorSession(aliensession.getID());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.base.AbstractSensor#getDisplayName()
	 */
	@Override
	@Property(displayName = "Display Name", description = "Logical Name of Reader", writable = true, type = PropertyType.PT_STRING, category = "connection", defaultValue = "Alien", orderValue = 0)
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the serverSocketPort
	 */
	@Property(displayName = "Notify Port", category = "connection", defaultValue = "54321", description = "The port configured in the Alien's Notify Address", type = PropertyType.PT_INTEGER, writable = true, minValue = "0", maxValue = "65535", orderValue = 1)
	public Integer getNotifyPort() {
		return notifyPort;
	}

	/**
	 * @param serverSocketPort
	 *            the serverSocketPort to set
	 */
	public void setNotifyPort(Integer serverSocketPort) {
		this.notifyPort = serverSocketPort;
	}

	/**
	 * @return the maxNumberAutonomousReaders
	 */
	@Property(category = "connection", displayName = "Maximum number of concurrent readers", defaultValue = "15", description = "The maximum number of Alien Readers that can send messages to this autonomous sensor simultaneously", minValue = "1", type = PropertyType.PT_INTEGER, writable = true)
	public Integer getMaxNumberAutonomousReaders() {
		return this.maxNumberAutonomousReaders;
	}

	/**
	 * @param maxNumberAutonomousReaders
	 *            the maxNumberAutonomousReaders to set
	 */
	public void setMaxNumberAutonomousReaders(Integer maxNumberAutonomousReaders) {
		if (maxNumberAutonomousReaders < 1) {
			throw new IllegalArgumentException(
					"maxNumberAutonomousReaders must be greater than 0");
		}
		this.maxNumberAutonomousReaders = maxNumberAutonomousReaders;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.configuration.RifidiService#getMBeanInfo()
	 */
	@Override
	public MBeanInfo getMBeanInfo() {
		return (MBeanInfo) mbeaninfo.clone();
	}

}
