/*
 *  LLRPReader.java
 *
 *  Created:	Mar 9, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.llrp;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.Destination;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.configuration.annotations.JMXMBean;
import org.rifidi.configuration.annotations.Property;
import org.rifidi.configuration.annotations.PropertyType;
import org.rifidi.edge.core.sensors.SensorSession;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
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
	private LLRPReaderSession session;
	/** A hashmap containing all the properties for this reader */
	private ConcurrentHashMap<String, String> readerProperties;
	/** IP address of the sensorSession. */
	private String ipAddress = "127.0.0.1";
	/** Port to connect to */
	private int port = 5084;
	/** Time between two connection attempts. */
	private Long reconnectionInterval = 500l;
	/** Number of connection attempts before a connection goes into fail state. */
	private Integer maxNumConnectionAttempts = 10;
	/** JMS destination. */
	private Destination destination;
	/** Spring JMS template */
	private JmsTemplate template;
	/** The ID of the session */
	private int sessionID = 0;
	/** A wrapper containing the service to send jms notifications */
	private NotifierService notifyServiceWrapper;

	/**
	 * 
	 */
	public LLRPReader() {
		this.readerProperties = new ConcurrentHashMap<String, String>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readers.AbstractReader#createReaderSession()
	 */
	@Override
	public synchronized SensorSession createReaderSession() {
		logger.debug("creating the reader session");
		if (session == null) {
			sessionID++;
			session = new LLRPReaderSession(this, Integer.toString(sessionID),
					ipAddress, (int) (long) reconnectionInterval,
					maxNumConnectionAttempts, destination, template,
					notifyServiceWrapper, super.getID());

			// TODO: remove this once we get AspectJ in here!
			NotifierService service = notifyServiceWrapper;
			if (service != null) {
				service.addSessionEvent(this.getID(), Integer
						.toString(sessionID));
			}
			return session;
		}
		return null;
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
	 * @see
	 * org.rifidi.edge.core.readers.AbstractReader#destroyReaderSession(org.
	 * rifidi.edge.core.readers.ReaderSession)
	 */
	@Override
	public void destroySensorSession(SensorSession session) {
		if (session != null) {
			for (Integer id : session.currentCommands().keySet()) {
				session.killComand(id);
			}
			this.session.disconnect();
			this.session = null;

			// TODO: remove this once we get AspectJ in here!
			NotifierService service = notifyServiceWrapper;
			if (service != null) {
				service.removeSessionEvent(this.getID(), Integer
						.toString(sessionID));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readers.AbstractReader#getReaderSessions()
	 */
	@Override
	public Map<String, SensorSession> getSensorSessions() {
		Map<String, SensorSession> ret = new HashMap<String, SensorSession>();
		if (session != null) {
			ret.put(session.getID(), session);
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
		session.transact(session.createSetReaderConfig());
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
			+ "reconnection attempts", writable = true, type = PropertyType.PT_LONG, category = "connec"
			+ "tion", orderValue = 3, defaultValue = LLRPConstants.RECONNECTION_INTERVAL)
	public Long getReconnectionInterval() {
		return reconnectionInterval;
	}

	/**
	 * Sets the reconnect interval.
	 * 
	 * @param reconnectionInterval
	 *            the reconnectionInterval to set
	 */
	public void setReconnectionInterval(Long reconnectionInterval) {
		this.reconnectionInterval = reconnectionInterval;
	}

	/**
	 * Gets the number of connection attempts to try before giving up.
	 * 
	 * @return the maxNumConnectionAttempts
	 */
	@Property(displayName = "Maximum Connection Attempts", description = "Number of times to try to connect to the sensorSession"
			+ ": before the connection is marked as failed.", writable = true, category = "conne"
			+ "ction", type = PropertyType.PT_INTEGER, orderValue = 2, defaultValue = LLRPConstants.MAX_CONNECTION_ATTEMPTS)
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
	 * @see org.rifidi.configuration.RifidiService#destroy()
	 */
	@Override
	public void destroy() {
		super.destroy();
		destroySensorSession(this.session);
	}
}
