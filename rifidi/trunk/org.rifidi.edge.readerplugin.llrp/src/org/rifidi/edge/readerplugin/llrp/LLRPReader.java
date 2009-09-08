/*
 * 
 * LLRPReader.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
package org.rifidi.edge.readerplugin.llrp;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.jms.Destination;
import javax.management.MBeanInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.rmi.dto.CommandDTO;
import org.rifidi.edge.api.rmi.dto.SessionDTO;
import org.rifidi.edge.core.configuration.annotations.JMXMBean;
import org.rifidi.edge.core.configuration.annotations.Property;
import org.rifidi.edge.core.configuration.annotations.PropertyType;
import org.rifidi.edge.core.configuration.mbeanstrategies.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.core.sensors.SensorSession;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.services.notification.NotifierService;
import org.springframework.jms.core.JmsTemplate;

/**
 * This class represents an LLRP reader. It handles the session and sets up the
 * properties.
 * 
 * @author Matthew Dean
 */
@JMXMBean
public class LLRPReader extends AbstractSensor<LLRPReaderSession> {
	/** Logger for this class. */
	private Log logger = LogFactory.getLog(LLRPReader.class);
	/** The only session an LLRP reader allows. */
	private AtomicReference<LLRPReaderSession> session = new AtomicReference<LLRPReaderSession>();
	/** A hashmap containing all the properties for this reader */
	private ConcurrentHashMap<String, String> readerProperties;
	/** IP address of the sensorSession. */
	private String ipAddress = "127.0.0.1";
	/** Port to connect to */
	private int port = 5084;
	/** Time between two connection attempts. */
	private volatile Integer reconnectionInterval = 500;
	/** Number of connection attempts before a connection goes into fail state. */
	private volatile Integer maxNumConnectionAttempts = 10;
	/** JMS destination. */
	private volatile Destination destination;
	/** Spring JMS template */
	private volatile JmsTemplate template;
	/** The ID of the session */
	private AtomicInteger sessionIDcounter = new AtomicInteger(0);
	/** A wrapper containing the service to send jms notifications */
	private NotifierService notifyServiceWrapper;
	/** Provided by spring. */
	private final Set<AbstractCommandConfiguration<?>> commands;
	/** Flag to check if this reader is destroied. */
	private AtomicBoolean destroied = new AtomicBoolean(false);
	/** Mbeaninfo for this class. */
	public static final MBeanInfo mbeaninfo;
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy.getMBeanInfo(LLRPReader.class);
	}

	/**
	 * Constructor.
	 */
	public LLRPReader(Set<AbstractCommandConfiguration<?>> commands) {
		this.commands = commands;
		this.readerProperties = new ConcurrentHashMap<String, String>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.AbstractSensor#createReaderSession(
	 * org.rifidi.edge.api.rmi.dto.SessionDTO)
	 */
	@Override
	public String createSensorSession(SessionDTO sessionDTO) {
		if (!destroied.get()) {
			synchronized (session) {
				LLRPReaderSession aliensession = session.get();
				if (aliensession == null) {
					Integer sessionID = Integer.parseInt(sessionDTO.getID());
					if (session.compareAndSet(null, new LLRPReaderSession(this,
							sessionID.toString(), ipAddress, port,
							reconnectionInterval, maxNumConnectionAttempts,
							destination, template, notifyServiceWrapper, super
									.getID(), commands))) {
						for (CommandDTO commandDTO : sessionDTO.getCommands()) {
							session.get().submit(commandDTO.getCommandID(),
									commandDTO.getInterval(),
									commandDTO.getTimeUnit());
						}
						// TODO: remove this once we get AspectJ in here!
						notifyServiceWrapper.addSessionEvent(this.getID(),
								Integer.toString(sessionID));
						return sessionID.toString();
					}
				}
				if (aliensession != null) {
					return aliensession.getID();
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readers.AbstractReader#createReaderSession()
	 */
	@Override
	public String createSensorSession() {
		if (!destroied.get()) {
			synchronized (session) {
				LLRPReaderSession aliensession = session.get();
				if (aliensession == null) {
					Integer sessionID = this.sessionIDcounter.incrementAndGet();
					if (session.compareAndSet(null, new LLRPReaderSession(this,
							sessionID.toString(), ipAddress, port,
							reconnectionInterval, maxNumConnectionAttempts,
							destination, template, notifyServiceWrapper, super
									.getID(), commands))) {

						// TODO: remove this once we get AspectJ in here!
						notifyServiceWrapper.addSessionEvent(this.getID(),
								Integer.toString(sessionID));
						return sessionID.toString();
					}
				}
				return aliensession.getID();
			}
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
	public void destroySensorSession(String sessionid) {
		LLRPReaderSession aliensession = session.get();
		if (session.compareAndSet(aliensession, null)) {
			if (aliensession != null && aliensession.getID().equals(sessionid)) {
				for (Integer id : aliensession.currentCommands().keySet()) {
					aliensession.killComand(id);
				}
				aliensession.disconnect();
				// TODO: remove this once we get AspectJ in here!
				notifyServiceWrapper
						.removeSessionEvent(this.getID(), sessionid);
			}
			logger.warn("Tried to delete a non existend session: " + sessionid);
		}
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

	/***
	 * Set the wrapper for the Notify Service.
	 * 
	 * @param wrapper
	 *            The JMS Notifier to set
	 */
	public void setNotifiyService(NotifierService wrapper) {
		this.notifyServiceWrapper = wrapper;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readers.AbstractReader#getReaderSessions()
	 */
	@Override
	public Map<String, SensorSession> getSensorSessions() {
		Map<String, SensorSession> ret = new HashMap<String, SensorSession>();
		LLRPReaderSession llrpsession = session.get();
		if (llrpsession != null) {
			ret.put(llrpsession.getID(), llrpsession);
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readers.AbstractReader#applyPropertyChanges()
	 */
	@Override
	public void applyPropertyChanges() {
		LLRPReaderSession readerSession = session.get();
		if (readerSession != null) {
			readerSession.transact(readerSession.createSetReaderConfig());
		}
	}

	/**
	 * Returns the IP address of the reader.
	 * 
	 * @return the ipAddress
	 */
	@Property(displayName = "IP Address", description = "IP Address of "
			+ "the Reader", writable = true, type = PropertyType.PT_STRING, category = "conn"
			+ "ection", orderValue = 0, defaultValue = LLRPConstants.LOCALHOST)
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * Sets the IP address of the reader.
	 * 
	 * @param ipAddress
	 *            the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * Gets the port.
	 * 
	 * @return the port
	 */
	@Property(displayName = "Port", description = "Port of the Reader", writable = true, type = PropertyType.PT_INTEGER, category = "c"
			+ "onnection", orderValue = 1, minValue = LLRPConstants.PORT_MIN, maxValue = LLRPConstants.PORT_MAX, defaultValue = LLRPConstants.PORT)
	public Integer getPort() {
		return port;
	}

	/**
	 * Sets the port.
	 * 
	 * @param port
	 *            the port to set
	 */
	public void setPort(Integer port) {
		this.port = port;
	}

	/**
	 * Gets the reconnect interval.
	 * 
	 * @return the reconnectionInterval
	 */
	@Property(displayName = "Reconnection Interval", description = "The interval to wait between"
			+ "reconnection attempts", writable = true, type = PropertyType.PT_INTEGER, category = "connec"
			+ "tion", orderValue = 3, defaultValue = LLRPConstants.RECONNECTION_INTERVAL)
	public Integer getReconnectionInterval() {
		return reconnectionInterval;
	}

	/**
	 * Sets the reconnect interval.
	 * 
	 * @param reconnectionInterval
	 *            the reconnectionInterval to set
	 */
	public void setReconnectionInterval(Integer reconnectionInterval) {
		this.reconnectionInterval = reconnectionInterval;
	}

	/**
	 * Gets the number of connection attempts to try before giving up.
	 * 
	 * @return the maxNumConnectionAttempts
	 */
	@Property(displayName = "Maximum Connection Attempts", description = "Number of times to attempt to connect to the reader."
			+ "  If -1, then try forever.", writable = true, category = "conne"
			+ "ction", type = PropertyType.PT_INTEGER, orderValue = 2, defaultValue = LLRPConstants.MAX_CONNECTION_ATTEMPTS, minValue = "-1")
	public Integer getMaxNumConnectionAttempts() {
		return maxNumConnectionAttempts;
	}

	/**
	 * Sets the number of connection attempts to try before giving up.
	 * 
	 * @param maxNumConnectionAttempts
	 *            the maxNumConnectionAttempts to set
	 */
	public void setMaxNumConnectionAttempts(Integer maxNumConnectionAttempts) {
		this.maxNumConnectionAttempts = maxNumConnectionAttempts;
	}

	/**
	 * Sets the JMS destination.
	 * 
	 * @param destination
	 *            the destination to set
	 */
	public void setDestination(Destination destination) {
		this.destination = destination;
	}

	/**
	 * Sets the JMS template.
	 * 
	 * @param template
	 *            the template to set
	 */
	public void setTemplate(JmsTemplate template) {
		this.template = template;
	}

	/**
	 * Gets the properties for the reader.
	 * 
	 * @return the readerProperties
	 */
	public ConcurrentHashMap<String, String> getReaderProperties() {
		return readerProperties;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.configuration.RifidiService#destroy()
	 */
	@Override
	public void destroy() {
		if (destroied.compareAndSet(false, true)) {
			super.destroy();
			LLRPReaderSession llrpsession = session.get();
			if (llrpsession != null) {
				destroySensorSession(llrpsession.getID());
			}
		}
	}
}
