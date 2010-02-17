/**
 * 
 */
package org.rifidi.edge.readerplugin.motorola.mc9090;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.management.MBeanInfo;

import org.rifidi.edge.api.rmi.dto.CommandDTO;
import org.rifidi.edge.api.rmi.dto.SessionDTO;
import org.rifidi.edge.core.configuration.annotations.JMXMBean;
import org.rifidi.edge.core.configuration.annotations.Property;
import org.rifidi.edge.core.configuration.annotations.PropertyType;
import org.rifidi.edge.core.exceptions.CannotCreateSessionException;
import org.rifidi.edge.core.sensors.SensorSession;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.services.notification.NotifierService;
import org.springframework.jms.core.JmsTemplate;

/**
 * A MC9090 Sensor. It creates sessions that open up a server socket and listen
 * for messages sent by the MC9090 reader. This behaves in a similar way to the
 * Alien9800 Autonomous Reader
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
@JMXMBean
public class MC9090Sensor extends AbstractSensor<MC9090Session> {

	/** The JMS template to use to send out tag notifications on */
	private JmsTemplate template;
	/** The FACTORY_ID of the session */
	private int sessionID = 0;
	/** The session created by this reader */
	private MC9090Session session;
	/** Notifier service to let clients know about events */
	private NotifierService notifierService;
	/** Flag to check if this reader is destroied. */
	private AtomicBoolean destroyed = new AtomicBoolean(false);
	/** The port of the server socket */
	private Integer serverSocketPort = 51284;
	/** Maximum number of autonomous readers supported concurrently */
	private Integer maxNumberAutonomousReaders = 15;
	/** Provided by spring. */
	private final Set<AbstractCommandConfiguration<?>> commands;

	public MC9090Sensor(Set<AbstractCommandConfiguration<?>> commands) {
		super();
		this.commands = commands;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.base.AbstractSensor#getSensorSessions()
	 */
	@Override
	public Map<String, SensorSession> getSensorSessions() {
		Map<String, SensorSession> map = new HashMap<String, SensorSession>();
		if (session != null) {
			map.put(session.getID(), session);
		}
		return map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.AbstractSensor#createSensorSession(
	 * org.rifidi.edge.api.rmi.dto.SessionDTO)
	 */
	@Override
	public synchronized String createSensorSession(SessionDTO sessionDTO)
			throws CannotCreateSessionException {
		if (session == null) {
			sessionID++;
			session = new MC9090Session(this, Integer.toString(sessionID),
					template, notifierService, this.serverSocketPort,
					maxNumberAutonomousReaders, commands);
			if (sessionDTO.getCommands() != null) {
				for (CommandDTO command : sessionDTO.getCommands()) {
					session.submit(command.getCommandID(), command
							.getInterval(), command.getTimeUnit());
				}
			}

			notifierService.addSessionEvent(getID(), Integer
					.toString(sessionID));
			return session.getID();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.AbstractSensor#createReaderSession()
	 */
	@Override
	public synchronized String createSensorSession() {
		if (session == null) {
			sessionID++;
			session = new MC9090Session(this, Integer.toString(sessionID),
					template, notifierService, this.serverSocketPort,
					maxNumberAutonomousReaders, commands);
			notifierService.addSessionEvent(getID(), Integer
					.toString(sessionID));
			return session.getID();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.AbstractSensor#destroySensorSession
	 * (java.lang.String)
	 */
	@Override
	public void destroySensorSession(String id) {
		if (session.getID().equals(id)) {
			session.disconnect();
			notifierService.removeSessionEvent(getID(), session.getID());
			this.session = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.configuration.RifidiService#destroy()
	 */
	@Override
	protected void destroy() {
		if (destroyed.compareAndSet(false, true)) {
			super.destroy();
			if (session != null) {
				destroySensorSession(session.getID());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.AbstractSensor#applyPropertyChanges()
	 */
	@Override
	public void applyPropertyChanges() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.configuration.services.RifidiService#getMBeanInfo()
	 */
	@Override
	public MBeanInfo getMBeanInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.base.AbstractSensor#getDisplayName()
	 */
	@Override
	protected String getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.AbstractSensor#unbindCommandConfiguration
	 * (org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration,
	 * java.util.Map)
	 */
	@Override
	public void unbindCommandConfiguration(
			AbstractCommandConfiguration<?> commandConfiguration,
			Map<?, ?> properties) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the serverSocketPort
	 */
	@Property(category = "connection", defaultValue = "51284", description = "The port to listen for MC9090 connections at", displayName = "Notify Port", maxValue = "65535", minValue = "0", orderValue = 0, type = PropertyType.PT_INTEGER, writable = true)
	public Integer getServerSocketPort() {
		return serverSocketPort;
	}

	/**
	 * @param serverSocketPort
	 *            the serverSocketPort to set
	 */
	public void setServerSocketPort(Integer serverSocketPort) {
		this.serverSocketPort = serverSocketPort;
	}

	/**
	 * @return the maxNumberAutonomousReaders
	 */
	@Property(category = "connection", defaultValue = "15", description = "Maximum number of MC9090 readers supported simultaneously", displayName = "Maximum number of concurrent readers", orderValue = 1, type = PropertyType.PT_INTEGER, writable = true)
	public Integer getMaxNumberAutonomousReaders() {
		return maxNumberAutonomousReaders;
	}

	/**
	 * @param maxNumberAutonomousReaders
	 *            the maxNumberAutonomousReaders to set
	 */
	public void setMaxNumberAutonomousReaders(Integer maxNumberAutonomousReaders) {
		this.maxNumberAutonomousReaders = maxNumberAutonomousReaders;
	}

	/**
	 * @param notifierService
	 *            the notifierService to set
	 */
	public void setNotifierService(NotifierService notifierService) {
		this.notifierService = notifierService;
	}

	/**
	 * @param template
	 *            the template to set
	 */
	public void setTemplate(JmsTemplate template) {
		this.template = template;
	}

}
