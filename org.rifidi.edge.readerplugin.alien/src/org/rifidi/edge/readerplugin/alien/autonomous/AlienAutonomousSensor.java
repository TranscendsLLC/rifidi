/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.autonomous;

import java.util.HashMap;
import java.util.Map;

import org.rifidi.configuration.annotations.JMXMBean;
import org.rifidi.configuration.annotations.Property;
import org.rifidi.configuration.annotations.PropertyType;
import org.rifidi.edge.core.sensors.SensorSession;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.services.notification.NotifierService;
import org.springframework.jms.core.JmsTemplate;

/**
 * A special sensor that is used to passively receive notifications from Alien
 * readers that are in autonomous mode. It cannot send commands to the Alien
 * reader
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
@JMXMBean
public class AlienAutonomousSensor
		extends
			AbstractSensor<AlienAutonomousSensorSession> {

	/** The session for this reader */
	private AlienAutonomousSensorSession session;
	/** The template to use to send tags */
	private JmsTemplate template;
	/** Notifier Service */
	private NotifierService notifierService;
	/** The FACTORY_ID of the session */
	private int sessionID = 0;
	/** The port of the server socket */
	private Integer serverSocketPort = 54321;
	/** Maximum number of autonomous readers supported concurrently */
	private Integer maxNumberAutonomousReaders = 15;

	/**
	 * @param template
	 *            the template to set
	 */
	public void setTemplate(JmsTemplate template) {
		this.template = template;
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
	 * org.rifidi.edge.core.sensors.base.AbstractSensor#createReaderSession()
	 */
	@Override
	public synchronized SensorSession createReaderSession() {
		if (session == null) {
			sessionID++;
			session = new AlienAutonomousSensorSession(this, Integer
					.toString(sessionID), template, notifierService,
					serverSocketPort, maxNumberAutonomousReaders);
			notifierService.addSessionEvent(getID(), Integer
					.toString(sessionID));
			return session;
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.AbstractSensor#destroyReaderSession
	 * (org.rifidi.edge.core.sensors.SensorSession)
	 */
	@Override
	public void destroySensorSession(SensorSession session) {
		if (session != null) {
			session.disconnect();
			notifierService.removeSessionEvent(getID(), session.getID());
			this.session = null;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.base.AbstractSensor#getReaderSessions()
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
	 * @see org.rifidi.edge.core.sensors.base.AbstractSensor#destroy()
	 */
	@Override
	public void destroy() {
		super.destroy();
		super.unregister();
		destroySensorSession(session);
	}

	/**
	 * @return the serverSocketPort
	 */
	@Property(displayName = "Notify Port", category = "connection", defaultValue = "54321", description = "The port configured in the Alien's Notify Address", type = PropertyType.PT_INTEGER, writable = true, minValue = "0", maxValue = "65535", orderValue=1)
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

}
