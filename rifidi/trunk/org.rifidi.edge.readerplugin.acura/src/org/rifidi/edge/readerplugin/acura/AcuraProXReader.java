/*
 *  AcuraProXReader.java
 *
 *  Created:	Dec 3, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.acura;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.management.MBeanInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

/**
 * The Acura reader. Currently impossible to test, as the reader itself is a
 * brick ATM.
 * 
 * @author Matthew Dean
 */
@JMXMBean
public class AcuraProXReader extends AbstractSensor<AcuraProXReaderSession> {

	/** Logger for this class. */
	private static final Log logger = LogFactory.getLog(AcuraProXReader.class);
	/** The ID of the session */
	private AtomicInteger sessionID = new AtomicInteger(0);
	/** The name of the reader that will be displayed */
	private String displayName = "Acura";
	/** Flag to check if this reader is destroyed. */
	private AtomicBoolean destroyed = new AtomicBoolean(false);
	/** The only session an acura reader allows. */
	private AtomicReference<AcuraProXReaderSession> session = new AtomicReference<AcuraProXReaderSession>();

	private String serialPort = null;

	/** Mbeaninfo for this class. */
	public static final MBeanInfo mbeaninfo;
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy.getMBeanInfo(AcuraProXReader.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.AbstractSensor#applyPropertyChanges()
	 */
	@Override
	public void applyPropertyChanges() {
		// No properties for this reader. Safely ignored.
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
			if (session.compareAndSet(null, new AcuraProXReaderSession(this,
					Integer.toString(sessionID), serialPort,
					notifierService, super.getID(), null))) {

				// TODO: remove this once we get AspectJ in here!
				notifierService.addSessionEvent(this.getID(), Integer
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
	 * org.rifidi.edge.core.sensors.base.AbstractSensor#createSensorSession(
	 * org.rifidi.edge.api.rmi.dto.SessionDTO)
	 */
	@Override
	public String createSensorSession(SessionDTO sessionDTO)
			throws CannotCreateSessionException {
		if (!destroyed.get() && session.get() == null) {
			Integer sessionID = this.sessionID.incrementAndGet();
			if (session.compareAndSet(null, new AcuraProXReaderSession(this,
					Integer.toString(sessionID), serialPort,
					notifierService, super.getID(),
					new HashSet<AbstractCommandConfiguration<?>>()))) {

				// TODO: remove this once we get AspectJ in here!
				notifierService.addSessionEvent(this.getID(), Integer
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
		AcuraProXReaderSession acurasession = session.get();
		if (acurasession != null && acurasession.getID().equals(id)) {
			session.set(null);
			acurasession.killAllCommands();
			acurasession.disconnect();
			// TODO: remove this once we get AspectJ in here!
			notifierService.removeSessionEvent(this.getID(), id);
		} else {
			String error = "Tried to delete a non existend session: " + id;
			logger.warn(error);
			throw new CannotDestroySensorException(error);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.base.AbstractSensor#getDisplayName()
	 */
	@Override
	@Property(displayName = "Display Name", description = "Logical "
			+ "Name of Reader", writable = true, type = PropertyType.PT_STRING, category = "connection", defaultValue = "Acura"
			+ "", orderValue = 0)
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.base.AbstractSensor#getSensorSessions()
	 */
	@Override
	public Map<String, SensorSession> getSensorSessions() {
		Map<String, SensorSession> ret = new HashMap<String, SensorSession>();
		AcuraProXReaderSession acurasession = session.get();
		if (acurasession != null) {
			ret.put(acurasession.getID(), acurasession);
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
		// No commands for this plugin, it can be safely ignored.
	}

	/**
	 * @return the MAX_CONNECTION_ATTEMPTS
	 */
	@Property(displayName = "Serial Port", description = "The serial port that the server "
			+ "will attempt to connect to", writable = true, type = PropertyType.PT_STRING, category = "connection"
			+ "", defaultValue = "COM1", orderValue = 3)
	public String getSerialPort() {
		return this.serialPort;
	}

	/**
	 * @param MAX_CONNECTION_ATTEMPTS
	 *            the MAX_CONNECTION_ATTEMPTS to set
	 */
	public void setSerialPort(String serialPort) {
		this.serialPort = serialPort;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.configuration.RifidiService#getMBeanInfo()
	 */
	@Override
	public MBeanInfo getMBeanInfo() {
		return (MBeanInfo) mbeaninfo.clone();
	}
}
