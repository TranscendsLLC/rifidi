/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.opticon;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.management.MBeanInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.SessionDTO;
import org.rifidi.edge.configuration.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.configuration.JMXMBean;
import org.rifidi.edge.configuration.Property;
import org.rifidi.edge.configuration.PropertyType;
import org.rifidi.edge.exceptions.CannotCreateSessionException;
import org.rifidi.edge.sensors.AbstractCommandConfiguration;
import org.rifidi.edge.sensors.AbstractSensor;
import org.rifidi.edge.sensors.CannotDestroySensorException;
import org.rifidi.edge.sensors.SensorSession;

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
	 * @see org.rifidi.edge.sensors.base.AbstractSensor#applyPropertyChanges()
	 */
	@Override
	public void applyPropertyChanges() {
		// Ignore; no properties.
	}

	/*
	 * (non-Javadoc) parity
	 * 
	 * @see org.rifidi.edge.sensors.base.AbstractSensor#createSensorSession()
	 */
	@Override
	public String createSensorSession() throws CannotCreateSessionException {
		if (!destroyed.get() && session.get() == null) {
			Integer sessionID = this.sessionID.incrementAndGet();
			if (session.compareAndSet(null, new OpticonSensorSession(this,
					Integer.toString(sessionID), notifierService,
					super.getID(), null, this.serialPortID))) {
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
	 * @see org.rifidi.edge.sensors.base.AbstractSensor#createSensorSession(
	 * org.rifidi.edge.api.rmi.dto.SessionDTO)
	 */
	@Override
	public String createSensorSession(SessionDTO sessionDTO)
			throws CannotCreateSessionException {
		if (!destroyed.get() && session.get() == null) {
			Integer sessionID = this.sessionID.incrementAndGet();
			if (session.compareAndSet(null, new OpticonSensorSession(this,
					Integer.toString(sessionID), notifierService,
					super.getID(), null, this.serialPortID))) {
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
	 * @see org.rifidi.edge.sensors.base.AbstractSensor#destroySensorSession
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
	 * @see org.rifidi.edge.sensors.base.AbstractSensor#getDisplayName()
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
	 * @see org.rifidi.edge.sensors.base.AbstractSensor#getSensorSessions()
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
	 * org.rifidi.edge.sensors.base.AbstractSensor#unbindCommandConfiguration
	 * (org.rifidi.edge.sensors.commands.AbstractCommandConfiguration,
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
			+ " or /dev/ttyUSB1 if it is set to VPC mode and connected via USB.  If the device "
			+ "is set to connect to /dev/Opticon it will attempt to figure out which /dev/ttyUSB "
			+ "connection /dev/Opticon is linked to.", writable = true, type = PropertyType.PT_STRING, category = ""
			+ "connection", defaultValue = "/dev/ttyUSB0", orderValue = 3)
	public String getSerialPort() {
		return this.serialPortID;
	}

	public void setSerialPort(String serialPortID) {
		String cmd = "ls -al /dev/Opticon";

		if (serialPortID.equalsIgnoreCase("/dev/Opticon")) {
			File f = new File("/dev/Opticon");
			if (f.exists()) {
				Runtime run = Runtime.getRuntime();
				Process pr = null;
				try {
					pr = run.exec(cmd);
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
				try {
					pr.waitFor();

				} catch (Exception e) {
					e.printStackTrace();
				}
				BufferedReader buf = new BufferedReader(new InputStreamReader(
						pr.getInputStream()));
				String dev = "";
				try {
					dev = buf.readLine();
				} catch (IOException e) {
					this.serialPortID = serialPortID;
					return;
				}
				char no = dev.charAt(dev.length() - 1);
				this.serialPortID = "/dev/ttyUSB" + no;

				logger.info("Comm port that the Opticon is being connected to: "
						+ this.serialPortID);

				// logger.info("AbstractSSS()  hope to see tty: "
				// + this.serialPortID);
				return;
			}
		}
		this.serialPortID = serialPortID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.configuration.services.RifidiService#getMBeanInfo()
	 */
	@Override
	public MBeanInfo getMBeanInfo() {
		return (MBeanInfo) mbeaninfo.clone();
	}

}
