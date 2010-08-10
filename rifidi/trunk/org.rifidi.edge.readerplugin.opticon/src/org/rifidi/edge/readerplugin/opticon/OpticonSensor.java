/*
 *  OpticonSensor.java
 *
 *  Created:	May 12, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.opticon;

import java.util.HashMap;
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
 * The Sensor class for the Opticon Barcode reader.  
 * 
 * @author Matthew Dean - matt@pramari.com
 */
@JMXMBean
public class OpticonSensor extends AbstractSensor<OpticonSensorSession> {

	/** Logger for this class. */
	private static final Log logger = LogFactory.getLog(OpticonSensor.class);
	/** Flag to check if this reader is destroyed. */
	private AtomicBoolean destroyed = new AtomicBoolean(false);
	/** The only session an Opticon reader allows. */
	private AtomicReference<OpticonSensorSession> session = new AtomicReference<OpticonSensorSession>();
	/** The name of the reader that will be displayed */
	private String displayName = "Opticon";
	/** The ID of the session */
	private AtomicInteger sessionID = new AtomicInteger(0);
	/** The ID for the serial port */
	private String serialPortID = "/dev/ttyUSB0";
	/** MBeanInfo for this class. */
	public static final MBeanInfo mbeaninfo;
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy.getMBeanInfo(OpticonSensor.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.AbstractSensor#applyPropertyChanges()
	 */
	@Override
	public void applyPropertyChanges() {
		// Ignore; no properties.
	}

	/*
	 * (non-Javadoc) parity
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.AbstractSensor#createSensorSession()
	 */
	@Override
	public String createSensorSession() throws CannotCreateSessionException {
		if (!destroyed.get() && session.get() == null) {
			Integer sessionID = this.sessionID.incrementAndGet();
			if (session.compareAndSet(null, new OpticonSensorSession(this,
					Integer.toString(sessionID),
					notifierService, super.getID(), null,
					this.serialPortID))) {
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
			if (session.compareAndSet(null, new OpticonSensorSession(this,
					Integer.toString(sessionID),
					notifierService, super.getID(), null,
					this.serialPortID))) {
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
		OpticonSensorSession opticonsession = session.get();
		if (opticonsession != null && opticonsession.getID().equals(id)) {
			session.set(null);
			opticonsession.disconnect();
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
		OpticonSensorSession opticonsession = session.get();
		if (opticonsession != null) {
			ret.put(opticonsession.getID(), opticonsession);
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
		// No commands
	}

	/**
	 * @return the The name of the Serial Port.  
	 */
	@Property(displayName = "Serial Port", description = "The serial port that the server "
			+ "will attempt to connect to.  On linux the opticon will default to /dev/ttyUSB0"
			+ " or /dev/ttyUSB1 if it is set to VPC mode and connected via USB. "
			+ " ", writable = true, type = PropertyType.PT_STRING, category = "connection"
			+ "", defaultValue = "/dev/ttyUSB0", orderValue = 3)
	public String getSerialPort() {
		return this.serialPortID;
	}

	public void setSerialPort(String serialPortID) {
		this.serialPortID = serialPortID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.configuration.services.RifidiService#getMBeanInfo()
	 */
	@Override
	public MBeanInfo getMBeanInfo() {
		return (MBeanInfo) mbeaninfo.clone();
	}

}
