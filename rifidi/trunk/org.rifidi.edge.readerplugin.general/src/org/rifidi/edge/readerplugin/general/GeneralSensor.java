/*
 *  GeneralSensor.java
 *
 *  Created:	Aug 5, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.general;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.management.MBeanInfo;

import org.rifidi.edge.api.rmi.dto.SessionDTO;
import org.rifidi.edge.core.configuration.annotations.JMXMBean;
import org.rifidi.edge.core.configuration.annotations.Property;
import org.rifidi.edge.core.configuration.annotations.PropertyType;
import org.rifidi.edge.core.configuration.mbeanstrategies.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.core.exceptions.CannotCreateSessionException;
import org.rifidi.edge.core.sensors.SensorSession;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.sensors.exceptions.CannotDestroySensorException;
import org.rifidi.edge.core.services.notification.NotifierService;
import org.springframework.jms.core.JmsTemplate;

/**
 * A generic plugin that can handle data coming in from any reader or source, if
 * it is sent to the serversocket in the correct format. The format is as
 * follows:
 * 
 * ID:(tag ID)|
 * 
 * @author Matthew Dean - matt@pramari.com
 */
@JMXMBean
public class GeneralSensor extends AbstractSensor<GeneralSensorSession> {

	/**
	 * 
	 */
	private Integer port = 4567;
	/** Spring JMS template */
	private volatile JmsTemplate template;
	/** The ID of the session */
	private AtomicInteger sessionID = new AtomicInteger(0);
	/** A wrapper containing the service to send JMS notifications */
	private volatile NotifierService notifyServiceWrapper;
	/** The name of the reader that will be displayed */
	private String displayName = "General";
	/** Flag to check if this reader is destroyed. */
	private AtomicBoolean destroyed = new AtomicBoolean(false);
	/** The only session an general reader allows. */
	private AtomicReference<GeneralSensorSession> session = new AtomicReference<GeneralSensorSession>();

	/** MBeanInfo for this class. */
	public static final MBeanInfo mbeaninfo;
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy.getMBeanInfo(GeneralSensor.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.AbstractSensor#applyPropertyChanges()
	 */
	@Override
	public void applyPropertyChanges() {
		//No properties.  
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.AbstractSensor#createSensorSession()
	 */
	@Override
	public String createSensorSession() throws CannotCreateSessionException {
		if (!destroyed.get() && session.get() == null) {
			Integer sessionID = this.sessionID.incrementAndGet();
			if (session.compareAndSet(null, new GeneralSensorSession(this,
					Integer.toString(sessionID), this.template,
					this.notifyServiceWrapper, super.getID(), this.port,
					new HashSet<AbstractCommandConfiguration<?>>()))) {
				// TODO: remove this once we get AspectJ in here!
				notifyServiceWrapper.addSessionEvent(this.getID(), Integer
						.toString(sessionID));
				return sessionID.toString();
			}
		}
		throw new CannotCreateSessionException();
	}

	/**
	 * Sets the JMSTemplate.
	 * 
	 * @param template
	 */
	public void setTemplate(JmsTemplate template) {
		this.template = template;
	}

	/**
	 * Gets the JMSTemplate
	 * 
	 * @return
	 */
	public JmsTemplate getTemplate() {
		return template;
	}

	/**
	 * Sets the NotifierService.  
	 * 
	 * @param notifyServiceWrapper
	 */
	public void setNotifyService(NotifierService notifyServiceWrapper) {
		this.notifyServiceWrapper = notifyServiceWrapper;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.AbstractSensor#createSensorSession(
	 * org.rifidi.edge.api.rmi.dto.SessionDTO)
	 */
	@Override
	public String createSensorSession(SessionDTO sessionDTO)
			throws CannotCreateSessionException {
		if (!destroyed.get() && session.get() == null) {
			Integer sessionID = this.sessionID.incrementAndGet();
			if (session.compareAndSet(null, new GeneralSensorSession(this,
					Integer.toString(sessionID), this.template,
					this.notifyServiceWrapper, super.getID(), this.port,
					new HashSet<AbstractCommandConfiguration<?>>()))) {
				// TODO: remove this once we get AspectJ in here!
				notifyServiceWrapper.addSessionEvent(this.getID(), Integer
						.toString(sessionID));
				return sessionID.toString();
			}
		}
		throw new CannotCreateSessionException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.AbstractSensor#destroySensorSession
	 * (java.lang.String)
	 */
	@Override
	public void destroySensorSession(String id)
			throws CannotDestroySensorException {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.base.AbstractSensor#getDisplayName()
	 */
	@Override
	protected String getDisplayName() {
		return displayName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.base.AbstractSensor#getSensorSessions()
	 */
	@Override
	public Map<String, SensorSession> getSensorSessions() {
		Map<String, SensorSession> ret = new HashMap<String, SensorSession>();
		GeneralSensorSession generalsession = session.get();
		if (generalsession != null) {
			ret.put(generalsession.getID(), generalsession);
		}
		return ret;
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

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.configuration.services.RifidiService#getMBeanInfo()
	 */
	@Override
	public MBeanInfo getMBeanInfo() {
		return mbeaninfo;
	}

	/**
	 * @return the The name of the Serial Port.
	 */
	@Property(displayName = "Port", description = "The port that the reader will listen for incoming "
			+ "connections from.", writable = true, type = PropertyType.PT_INTEGER, category = "connection"
			+ "", defaultValue = "4567", orderValue = 1)
	public Integer getPort() {
		return this.port;
	}

	/**
	 * Sets the port for the reader.
	 * 
	 * @param port
	 */
	public void setPort(Integer port) {
		this.port = port;
	}

}
