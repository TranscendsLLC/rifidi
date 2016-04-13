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
package org.rifidi.edge.adapter.awid.awid2010;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
 * The Awid Sensor. It produces AwidSessions.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
@JMXMBean
public class AwidSensor extends AbstractSensor<AwidSession> {

	/** The logger for this class */
	private final static Log logger = LogFactory.getLog(AwidSensor.class);
	/** The IP address of the Awid */
	private volatile String host = AwidDefaultValues.HOST;
	/** */
	private Boolean disableAutoStart=false;
	/** The port of the Awid */
	private volatile Integer port = Integer.parseInt(AwidDefaultValues.PORT);
	/** The maximum number of reconnection attempts before giving up */
	private volatile Integer maxNumConnectionAttempts = Integer
			.parseInt(AwidDefaultValues.MAX_NUM_RECON_ATTEMPS);
	/** The time to wait between successive reconnection attempts */
	private volatile Integer reconnectionInterval = Integer
			.parseInt(AwidDefaultValues.RECON_INTERVAL);
	/** The session, if it has been created */
	private final AtomicReference<AwidSession> session = new AtomicReference<AwidSession>();
	/** True if this sensor has been destroyed */
	private final AtomicBoolean destroyed = new AtomicBoolean(false);
	/** The counter that keeps track of session IDs */
	private AtomicInteger sessionID = new AtomicInteger(0);
	/** Provided by spring. */
	private final Set<AbstractCommandConfiguration<?>> commands;
	/** Is the reader a 3014? */
	private boolean is3014 = false;
	/** Mbeaninfo for this class. */
	public static final MBeanInfo mbeaninfo;
	private String displayName = "Awid2010";
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy.getMBeanInfo(AwidSensor.class);
	}

	/**
	 * Constructor
	 * 
	 * @param commands
	 */
	public AwidSensor(Set<AbstractCommandConfiguration<?>> commands,
			boolean is3014) {
		super();
		this.commands = commands;
		this.is3014 = is3014;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.base.AbstractSensor#getSensorSessions()
	 */
	@Override
	public Map<String, SensorSession> getSensorSessions() {
		Map<String, SensorSession> retVal = new HashMap<String, SensorSession>();
		AwidSession awidSession = session.get();
		if (awidSession != null) {
			retVal.put(awidSession.getID(), awidSession);
		}
		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.base.AbstractSensor#createSensorSession()
	 */
	@Override
	public String createSensorSession() throws CannotCreateSessionException {
		if (!destroyed.get() && session.get() == null) {
			Integer sessionID = this.sessionID.incrementAndGet();
			if (session.compareAndSet(null,
					new AwidSession(this, Integer.toString(sessionID), host,
							port, reconnectionInterval,
							maxNumConnectionAttempts, commands,
							notifierService, is3014))) {

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
	 * org.rifidi.edge.sensors.base.AbstractSensor#createSensorSession(
	 * org.rifidi.edge.api.rmi.dto.SessionDTO)
	 */
	@Override
	public String createSensorSession(SessionDTO sessionDTO)
			throws CannotCreateSessionException {
		if (!destroyed.get() && session.get() == null) {
			Integer sessionID = Integer.parseInt(sessionDTO.getID());
			if (session.compareAndSet(null,
					new AwidSession(this, Integer.toString(sessionID), host,
							port, reconnectionInterval,
							maxNumConnectionAttempts, commands,
							notifierService, is3014))) {

				this.session.get().restoreCommands(sessionDTO);

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
	 * org.rifidi.edge.sensors.base.AbstractSensor#destroySensorSession
	 * (java.lang.String)
	 */
	@Override
	public void destroySensorSession(String id)
			throws CannotDestroySensorException {
		AwidSession awidSession = session.get();
		if (awidSession != null && awidSession.getID().equals(id)) {
			session.set(null);
			awidSession.killAllCommands();
			awidSession.disconnect();
			awidSession.destroy();
			// TODO: remove this once we get AspectJ in here!
			notifierService.removeSessionEvent(this.getID(), id);
		} else {
			String error = "Tried to delete a non existend session: " + id;
			throw new CannotDestroySensorException(error);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.base.AbstractSensor#destroy()
	 */
	@Override
	protected void destroy() {
		if (destroyed.compareAndSet(false, true)) {
			super.destroy();
			AwidSession awidSession = session.get();
			if (awidSession != null) {
				try {
					destroySensorSession(awidSession.getID());
				} catch (CannotDestroySensorException e) {
					logger.warn("Cannot Destroy Session: ", e);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.base.AbstractSensor#applyPropertyChanges()
	 */
	@Override
	public void applyPropertyChanges() {
		// Implement this method if we need to send any property changes to the
		// reader

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
		if (!destroyed.get()) {
			AwidSession awidSession = session.get();
			if (awidSession != null) {
				awidSession.suspendCommand(commandConfiguration.getID());
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.base.AbstractSensor#getDisplayName()
	 */
	@Override
	@Property(displayName = "Display Name", description = "Logical Name of Reader", writable = true, type = PropertyType.PT_STRING, category = "connection", defaultValue = "Awid", orderValue = 0)
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the host
	 */
	@Property(displayName = "IP Address", description = "IP Address of the Reader", writable = true, type = PropertyType.PT_STRING, category = "connection", defaultValue = AwidDefaultValues.HOST, orderValue = 0)
	public String getHost() {
		return host;
	}

	/**
	 * @param host
	 *            the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the port
	 */
	@Property(displayName = "Port", description = "Port of the Reader", writable = true, type = PropertyType.PT_INTEGER, category = "connection", orderValue = 1, defaultValue = AwidDefaultValues.PORT, minValue = "0", maxValue = "65535")
	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}
	
	@Property(displayName = "DisableAutoStart", description = "Set to false to disable autostart", writable = true, type = PropertyType.PT_BOOLEAN, 
			category = "connection", orderValue = 8, defaultValue = "false")
	public Boolean getAutoStart() {
		return disableAutoStart;
	}
	public void setAutoStart(Boolean autoStart) {
		this.disableAutoStart = autoStart;
	}

	/**
	 * @return the maxNumConnectionAttempts
	 */
	@Property(displayName = "Maximum Connection Attempts", description = "Upon connection failure, the number of times to attempt to recconnect before giving up. If set to '-1', then try forever", writable = true, type = PropertyType.PT_INTEGER, category = "connection", defaultValue = AwidDefaultValues.MAX_NUM_RECON_ATTEMPS, orderValue = 2, minValue = "-1")
	public Integer getMaxNumConnectionAttempts() {
		return maxNumConnectionAttempts;
	}

	/**
	 * @param maxNumConnectionAttempts
	 *            the maxNumConnectionAttempts to set
	 */
	public void setMaxNumConnectionAttempts(Integer maxNumConnectionAttempts) {
		this.maxNumConnectionAttempts = maxNumConnectionAttempts;
	}

	/**
	 * @return the reconnectionInterval
	 */
	@Property(displayName = "Reconnection Interval", description = "Upon connection failure, the time to wait between two connection attempts (ms)", writable = true, type = PropertyType.PT_INTEGER, category = "connection", defaultValue = AwidDefaultValues.RECON_INTERVAL, orderValue = 3, minValue = "0")
	public Integer getReconnectionInterval() {
		return reconnectionInterval;
	}

	/**
	 * @param reconnectionInterval
	 *            the reconnectionInterval to set
	 */
	public void setReconnectionInterval(Integer reconnectionInterval) {
		this.reconnectionInterval = reconnectionInterval;
	}
}
