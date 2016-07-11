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
package org.rifidi.edge.adapter.generic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.management.MBeanInfo;

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
 * A generic plugin that can handle data coming in from any reader or source, if
 * it is sent to the serversocket in the correct format. The format is as
 * follows:
 * 
 * ID:(tag ID)|Antenna:(antenna)|Timestamp:(millis since epoch)
 * 
 * Any amount of other values can be specified in pairs delimited by colons,
 * with pipes delimiting pairs.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
@JMXMBean
public class GenericSensor extends AbstractSensor<GenericSensorSession> {

	/**
	 * 
	 */
	private Integer port = 4567;
	private Integer mqttPort = -1;	
	private Integer restPort = -1;
	
	/** The ID of the session */
	private AtomicInteger sessionID = new AtomicInteger(0);
	/** The name of the reader that will be displayed */
	private String displayName = "Generic";
	/** Flag to check if this reader is destroyed. */
	private AtomicBoolean destroyed = new AtomicBoolean(false);
	/** The only session an generic reader allows. */
	private AtomicReference<GenericSensorSession> session = new AtomicReference<GenericSensorSession>();
	
	private Boolean disableAutoStart=false;
	

	/** MBeanInfo for this class. */
	public static final MBeanInfo mbeaninfo;
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy.getMBeanInfo(GenericSensor.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.base.AbstractSensor#applyPropertyChanges()
	 */
	@Override
	public void applyPropertyChanges() {
		// No properties.
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
			if (session.compareAndSet(null, new GenericSensorSession(this,
					Integer.toString(sessionID), notifierService,
					super.getID(), this.port, this.restPort, this.mqttPort,
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
	 * org.rifidi.edge.sensors.base.AbstractSensor#createSensorSession(
	 * org.rifidi.edge.api.rmi.dto.SessionDTO)
	 */
	@Override
	public String createSensorSession(SessionDTO sessionDTO)
			throws CannotCreateSessionException {
		if (!destroyed.get() && session.get() == null) {
			Integer sessionID = this.sessionID.incrementAndGet();
			if (session.compareAndSet(null, new GenericSensorSession(this,
					Integer.toString(sessionID), notifierService,
					super.getID(), this.port, this.restPort, this.mqttPort,
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
	 * org.rifidi.edge.sensors.base.AbstractSensor#destroySensorSession
	 * (java.lang.String)
	 */
	@Override
	public void destroySensorSession(String id)
			throws CannotDestroySensorException {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.base.AbstractSensor#getSensorSessions()
	 */
	@Override
	public Map<String, SensorSession> getSensorSessions() {
		Map<String, SensorSession> ret = new HashMap<String, SensorSession>();
		GenericSensorSession genericsession = session.get();
		if (genericsession != null) {
			ret.put(genericsession.getID(), genericsession);
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

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.configuration.services.RifidiService#getMBeanInfo()
	 */
	@Override
	public MBeanInfo getMBeanInfo() {
		return mbeaninfo;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.base.AbstractSensor#getDisplayName()
	 */
	@Override
	@Property(displayName = "Display Name", description = "Logical Name of Reader", writable = true, type = PropertyType.PT_STRING, category = "connection", defaultValue = "Generic", orderValue = 0)
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
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
	
	/**
	 * @return the The name of the Serial Port.
	 */
	@Property(displayName = "MqttPort", description = "Sets a port for incoming mqtt connections, "
			+ "-1 to disable.", writable = true, type = PropertyType.PT_INTEGER, category = "connection"
			+ "", defaultValue = "-1", orderValue = 2)
	public Integer getMqttPort() {
		return this.mqttPort;
	}

	/**
	 * Sets the port for the reader.
	 * 
	 * @param port
	 */
	public void setMqttPort(Integer port) {
		this.mqttPort = port;
	}
	
	/**
	 * @return the The name of the Serial Port.
	 */
	@Property(displayName = "RestPort", description = "Sets a port for incoming rest connections, "
			+ "-1 to disable.", writable = true, type = PropertyType.PT_INTEGER, category = "connection"
			+ "", defaultValue = "-1", orderValue = 3)
	public Integer getRestPort() {
		return this.restPort;
	}

	/**
	 * Sets the port for the reader.
	 * 
	 * @param port
	 */
	public void setRestPort(Integer port) {
		this.restPort = port;
	}
	
	
	
	@Property(displayName = "DisableAutoStart", description = "Set to true to disable autostart", writable = true, type = PropertyType.PT_BOOLEAN, 
			category = "connection", orderValue = 8, defaultValue = "false")
	public Boolean getDisableAutoStart() {
		return disableAutoStart;
	}
	public void setDisableAutoStart(Boolean disableAutoStart) {
		this.disableAutoStart = disableAutoStart;
	}

}
