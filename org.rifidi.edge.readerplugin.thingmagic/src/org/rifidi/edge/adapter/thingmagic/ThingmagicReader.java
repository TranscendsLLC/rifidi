/*
 *  ThingmagicReader.java
 *
 *  Created:	Sep 15, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.adapter.thingmagic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.management.MBeanInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.adapter.thingmagic.commandobject.ThingmagicCommandObjectWrapper;
import org.rifidi.edge.adapter.thingmagic.commands.internal.ThingmagicPropertyCommand;
import org.rifidi.edge.api.rmi.dto.CommandDTO;
import org.rifidi.edge.api.rmi.dto.SessionDTO;
import org.rifidi.edge.core.configuration.annotations.JMXMBean;
import org.rifidi.edge.core.configuration.annotations.Operation;
import org.rifidi.edge.core.configuration.annotations.Property;
import org.rifidi.edge.core.configuration.annotations.PropertyType;
import org.rifidi.edge.core.configuration.mbeanstrategies.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.core.exceptions.CannotCreateSessionException;
import org.rifidi.edge.core.sensors.SensorSession;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;

/**
 * This plugin connects to the Thingmagic Reader.
 * 
 * @author Matthew Dean
 */
@JMXMBean
public class ThingmagicReader extends AbstractSensor<ThingmagicReaderSession> {

	/** Logger for this class. */
	private static final Log logger = LogFactory.getLog(ThingmagicReader.class);
	/** The session for the reader. */
	private AtomicReference<ThingmagicReaderSession> session = new AtomicReference<ThingmagicReaderSession>();
	/** Flag to check if this reader is destroied. */
	private AtomicBoolean destroied = new AtomicBoolean(false);
	/** Time between two connection attempts. */
	private volatile Integer reconnectionInterval = Integer
			.parseInt(ThingmagicReaderDefaultValues.RECONNECTION_INTERVAL);
	/** Number of connection attempts before a connection goes into fail state. */
	private volatile Integer maxNumConnectionAttempts = Integer
			.parseInt(ThingmagicReaderDefaultValues.MAX_CONNECTION_ATTEMPTS);
	/** IP address of the sensorSession. */
	private volatile String ipAddress = ThingmagicReaderDefaultValues.IPADDRESS;
	/** Port to connect to. */
	private volatile Integer port = Integer
			.parseInt(ThingmagicReaderDefaultValues.PORT);
	/** A hashmap containing all the properties for this reader */
	private final ConcurrentHashMap<String, String> readerProperties;
	/** A queue for putting commands to be executed next */
	private final LinkedBlockingQueue<ThingmagicCommandObjectWrapper> propCommandsToBeExecuted;
	private String displayName = "ThingMagic";

	/** Mbeaninfo for this class. */
	public static final MBeanInfo mbeaninfo;
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy.getMBeanInfo(ThingmagicReader.class);
	}
	
	/** Provided by spring. */
	private final Set<AbstractCommandConfiguration<?>> commands;
	/** The FACTORY_ID of the session */
	private AtomicInteger sessionID = new AtomicInteger(0);

	/**
	 * Constructor.
	 */
	public ThingmagicReader(Set<AbstractCommandConfiguration<?>> commands) {
		this.commands = commands;
		readerProperties = new ConcurrentHashMap<String, String>();
		propCommandsToBeExecuted = new LinkedBlockingQueue<ThingmagicCommandObjectWrapper>();
		logger.debug("New instance of ThingmagicReader created.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.AbstractSensor#applyPropertyChanges()
	 */
	@Override
	@Operation(description = "Apply all property changes to reader")
	public void applyPropertyChanges() {
		// TODO: may need to synchnonize the hashmap before I clear it?
		ThingmagicReaderSession thingmagicSession = session.get();
		if (thingmagicSession != null) {
			ArrayList<ThingmagicCommandObjectWrapper> commands = new ArrayList<ThingmagicCommandObjectWrapper>();
			this.propCommandsToBeExecuted.drainTo(commands);
			ThingmagicPropertyCommand command = new ThingmagicPropertyCommand(
					"", readerProperties, commands);
			thingmagicSession.submit(command);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.AbstractSensor#createSensorSession()
	 */
	@Override
	public String createSensorSession() throws CannotCreateSessionException {
		if (!destroied.get() && session.get() == null) {
			Integer sessionID = this.sessionID.incrementAndGet();
			if (session.compareAndSet(null, new ThingmagicReaderSession(this,
					Integer.toString(sessionID), ipAddress, port,
					(int) (long) reconnectionInterval,
					maxNumConnectionAttempts, notifierService, this
							.getID(), commands))) {

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
		if (!destroied.get() && session.get() == null) {
			Integer sessionID = Integer.parseInt(sessionDTO.getID());
			if (session.compareAndSet(null, new ThingmagicReaderSession(this,
					Integer.toString(sessionID), ipAddress, port,
					(int) (long) reconnectionInterval,
					maxNumConnectionAttempts,  notifierService, this
							.getID(), commands))) {
				for (CommandDTO commandDTO : sessionDTO.getCommands()) {
					session.get().submit(commandDTO.getCommandID(),
							commandDTO.getInterval(), commandDTO.getTimeUnit());
				}
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
	public void destroySensorSession(String sessionid) {
		ThingmagicReaderSession thingmagicsession = session.getAndSet(null);
		if (thingmagicsession != null
				&& thingmagicsession.getID().equals(sessionid)) {
			thingmagicsession.killAllCommands();
			thingmagicsession.disconnect();
			// TODO: remove this once we get AspectJ in here!
			notifierService.removeSessionEvent(this.getID(), sessionid);
		}
		logger.warn("Tried to delete a non existend session: " + sessionid);
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.base.AbstractSensor#getDisplayName()
	 */
	@Override
	@Property(displayName="Display Name", 
			description="Logical Name of Reader",
			writable=true,
			type=PropertyType.PT_STRING,
			category="connection",
			defaultValue="ThingMagic",
			orderValue=0)
	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String displayName){
		this.displayName = displayName;
	}

	/**
	 * @return the IPADDRESS
	 */
	@Property(displayName = "IP Address", description = "IP Address of "
			+ "the Reader", writable = true, type = PropertyType.PT_STRING, category = "conn"
			+ "ection", defaultValue = ThingmagicReaderDefaultValues.IPADDRESS, orderValue = 0)
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @param IPADDRESS
	 *            the IPADDRESS to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * @return the PORT
	 */
	// 
	@Property(displayName = "Port", description = "Port of the" + " Reader", writable = true, type = PropertyType.PT_INTEGER, category = "conn"
			+ "ection", orderValue = 1, defaultValue = ThingmagicReaderDefaultValues.PORT, minValue = "0", maxValue = "65535")
	public Integer getPort() {
		return port;
	}

	/**
	 * @param PORT
	 *            the PORT to set
	 */
	public void setPort(Integer port) {
		this.port = port;
	}

	/**
	 * @return the RECONNECTION_INTERVAL
	 */
	@Property(displayName = "Reconnection Interval", description = "Upon connection failure, the time to wait between two connection attempts (ms)", writable = true, type = PropertyType.PT_INTEGER, category = "conn"
			+ "ection", defaultValue = ThingmagicReaderDefaultValues.RECONNECTION_INTERVAL, orderValue = 4, minValue = "0")
	public Integer getReconnectionInterval() {
		return reconnectionInterval;
	}

	/**
	 * @param RECONNECTION_INTERVAL
	 *            the RECONNECTION_INTERVAL to set
	 */
	public void setReconnectionInterval(Integer reconnectionInterval) {
		this.reconnectionInterval = reconnectionInterval;
	}

	/**
	 * @return the MAX_CONNECTION_ATTEMPTS
	 */
	@Property(displayName = "Maximum Connection Attempts", description = "Upon connection failure, the number of times to attempt to recconnect before giving up. If set to '-1', then try forever", writable = true, type = PropertyType.PT_INTEGER, category = "conn"
			+ "ection", defaultValue = ThingmagicReaderDefaultValues.MAX_CONNECTION_ATTEMPTS, orderValue = 5, minValue = "-1")
	public Integer getMaxNumConnectionAttempts() {
		return maxNumConnectionAttempts;
	}

	/**
	 * @param MAX_CONNECTION_ATTEMPTS
	 *            the MAX_CONNECTION_ATTEMPTS to set
	 */
	public void setMaxNumConnectionAttempts(Integer maxNumConnectionAttempts) {
		this.maxNumConnectionAttempts = maxNumConnectionAttempts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.base.AbstractSensor#getSensorSessions()
	 */
	@Override
	public Map<String, SensorSession> getSensorSessions() {
		Map<String, SensorSession> ret = new HashMap<String, SensorSession>();
		ThingmagicReaderSession thingmagicsession = session.get();
		if (thingmagicsession != null) {
			ret.put(thingmagicsession.getID(), thingmagicsession);
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
		if (!destroied.get()) {
			ThingmagicReaderSession thingmagicsession = session.get();
			if (thingmagicsession != null) {
				thingmagicsession.suspendCommand(commandConfiguration.getID());
			}
		}
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
