/**
 * 
 */
package org.rifidi.edge.readerplugin.alien;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jms.Destination;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.configuration.annotations.JMXMBean;
import org.rifidi.configuration.annotations.Property;
import org.rifidi.configuration.annotations.PropertyType;
import org.rifidi.edge.core.readers.AbstractReader;
import org.rifidi.edge.core.readers.ReaderSession;
import org.springframework.jms.core.JmsTemplate;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
@JMXMBean
public class Alien9800Reader extends AbstractReader<Alien9800ReaderSession> {
	/** Logger for this class. */
	private Log logger = LogFactory.getLog(Alien9800Reader.class);
	/** Description of the readerSession. */
	private static final String description = "The Alien 9800 is an IP based RFID ReaderSession using a telnet interface.";
	/** Name of the readerSession. */
	private static final String name = "Alien9800";
	/** The only session an alien reader allows. */
	private Alien9800ReaderSession session;

	/***
	 * READER PROPERTIES - CONNECTION INFO
	 */
	private HashMap<String, String> readerProperties;
	/** IP address of the readerSession. */
	private String ipAddress = "127.0.0.1";
	/** Port to connect to. */
	private Integer port = 20000;
	/** Username for the telnet interface. */
	private String username = "alien";
	/** Password for the telnet interface. */
	private String password = "password";
	/** Time between two connection attempts. */
	private Long reconnectionInterval = 500l;
	/** Number of connection attempts before a connection goes into fail state. */
	private Integer maxNumConnectionAttempts = 10;
	/** JMS destination. */
	private Destination destination;
	/** Spring JMS template*/
	private JmsTemplate template;

	/**
	 * READER PROPERTIES - SETTABE, SET ON CONNECTION
	 */
	public static final String PROP_RF_ATTENUATION = "RFAttenuation";
	public static final String PROP_EXTERNAL_OUTPUT = "externalOutput";
	public static final String PROP_INVERT_EXTERNAL_INPUT = "invertExternalInput";
	public static final String PROP_INVERT_EXTERNAL_OUTPUT = "inverExternalOutput";

	/**
	 * READER PROPERTIES - SETTABLE, INITIALIZED BY AQUIRE READER PROPERTIES
	 */
	public static final String PROP_COMMAND_PORT = "commandPort";
	public static final String PROP_DHCP = "dhcp";
	public static final String PROP_DNS = "dns";
	public static final String PROP_GATEWAY = "gateway";
	private String heartbeat_address;
	private String heartbeat_count;
	private String heartbeat_port;
	private String heartbeat_time;
	private String netmask;
	private String network_timeout;
	private String readername;
	private String time;
	private String time_server;
	private String time_zone;
	/** The unique number of the readerSession */
	public static final String PROP_READER_NUMBER = "readerNumber";

	/**
	 * READER PROPERTIES - READ ONLY, INITIALZIED BY AQUIRE READER PROPERTIES
	 */
	/** MAC Address of the readerSession */
	public static final String PROP_MAC_ADDRESS = "macAddress";
	/** Maximum number of antennas supported */
	public static final String PROP_MAX_ANTENNA = "maxAntenna";
	/** The type of the alien readerSession */
	public static final String PROP_READER_TYPE = "readerType";
	/** The version of the readerSession */
	public static final String PROP_READER_VERSION = "readerVersion";
	/** GPO value */
	public static final String PROP_EXTERNAL_INPUT = "externalinput";
	/** Uptime of the readerSession */
	public static final String PROP_UPTIME = "uptime";

	/**
	 * Constructor.
	 */
	public Alien9800Reader() {
		readerProperties = new HashMap<String, String>();
		readerProperties.put(PROP_READER_NUMBER, "0");
		readerProperties.put(PROP_READER_VERSION, "Unavailable");
		readerProperties.put(PROP_READER_TYPE, "Unavailable");
		readerProperties.put(PROP_MAC_ADDRESS, "Unavailable");
		readerProperties.put(PROP_MAX_ANTENNA, "0");
		readerProperties.put(PROP_EXTERNAL_INPUT, "0");
		readerProperties.put(PROP_UPTIME, "0");
		readerProperties.put(PROP_EXTERNAL_OUTPUT, "0");
		readerProperties.put(PROP_RF_ATTENUATION, "0");
		logger
				.debug("New instance of Alien 9800 ReaderSession config created.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.ReaderConfiguration#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.ReaderConfiguration#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readers.AbstractReader#createReaderSession()
	 */
	@Override
	public synchronized ReaderSession createReaderSession() {
		if (session == null) {
			session = new Alien9800ReaderSession(ipAddress, port,
					(int) (long) reconnectionInterval,
					maxNumConnectionAttempts, username, password, destination, template);
			return session;
		}
		return null;
	}
	
	/**
	 * @param destination the destination to set
	 */
	public void setDestination(Destination destination) {
		this.destination = destination;
	}

	/**
	 * @param template the template to set
	 */
	public void setTemplate(JmsTemplate template) {
		this.template = template;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readers.AbstractReader#destroyReaderSession(org.
	 * rifidi.edge.core.readers.ReaderSession)
	 */
	@Override
	public void destroyReaderSession(ReaderSession session) {
		session.disconnect();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readers.AbstractReader#getReaderSessions()
	 */
	@Override
	public List<ReaderSession> getReaderSessions() {
		List<ReaderSession> ret = new ArrayList<ReaderSession>();
		if (session != null) {
			ret.add(session);
		}
		return ret;
	}

	/*
	 * JMX PROPERTY GETTER/SETTERS
	 */

	/**
	 * @return the ipAddress
	 */
	@Property(displayName = "IP Address", description = "Address of the readerSession.", writable = true)
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @param ipAddress
	 *            the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * @return the port
	 */
	@Property(displayName = "Port", description = "Port of the readerSession.", writable = true, type = PropertyType.PT_INTEGER)
	public Integer getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(Integer port) {
		this.port = port;
	}

	/**
	 * @return the username
	 */
	@Property(displayName = "Username", description = "Username for logging into the readerSession.", writable = true)
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	@Property(displayName = "Password", description = "Password for logging into the readerSession.", writable = true)
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the reconnectionInterval
	 */
	@Property(displayName = "Reconnection Interval", description = "Time between two connection attempts (ms).", writable = true, type = PropertyType.PT_LONG)
	public Long getReconnectionInterval() {
		return reconnectionInterval;
	}

	/**
	 * @param reconnectionInterval
	 *            the reconnectionInterval to set
	 */
	public void setReconnectionInterval(Long reconnectionInterval) {
		this.reconnectionInterval = reconnectionInterval;
	}

	/**
	 * @return the maxNumConnectionAttempts
	 */
	@Property(displayName = "Maximum Connection Attempts", description = "Number of times to try to connect to the readerSession before the connection is marked as failed.", writable = true, type = PropertyType.PT_INTEGER)
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

	@Property(displayName = "GPO Output", description = "Ouput of GPO", writable = true, type = PropertyType.PT_INTEGER, minValue = "0", maxValue = "255")
	public Integer getExternalOutput() {
		return Integer.parseInt(readerProperties.get(PROP_EXTERNAL_OUTPUT));
	}

	public void setExternalOutput(Integer externalOutput) {
		if (externalOutput >= 0 && externalOutput <= 255) {
			readerProperties.put(PROP_EXTERNAL_OUTPUT, Integer
					.toString(externalOutput));
			return;
		}
		logger.warn("ExternalOutput must be an"
				+ " integer between 0 and 255, but was " + externalOutput);
	}

	@Property(displayName = "RF Attenuation", description = "RF Attenuation", writable = true, type = PropertyType.PT_INTEGER)
	public Integer getRFAttenuation() {
		return Integer.parseInt(readerProperties.get(PROP_RF_ATTENUATION));
	}

	public void setRFAttenuation(Integer rfAttenuation) {
		readerProperties.put(PROP_RF_ATTENUATION, Integer
				.toString(rfAttenuation));
	}

	@Property(displayName = "ReaderSession Version", description = "Version Number of the readerSession", writable = false)
	public String getReaderVersion() {
		return (String) readerProperties.get(PROP_READER_VERSION);
	}

	@Property(displayName = "ReaderSession Type", description = "Type of ReaderSession", writable = false)
	public String getReaderType() {
		return (String) readerProperties.get(PROP_READER_TYPE);
	}

	@Property(displayName = "Max Antennas", description = "Maximum number of antennas", writable = false, type = PropertyType.PT_INTEGER)
	public Integer getMaxAntennas() {
		return Integer.parseInt(readerProperties.get(PROP_MAX_ANTENNA));
	}

	@Property(displayName = "MAC Address", description = "MAC Address of readerSession", writable = false)
	public String getMACAddress() {
		return (String) readerProperties.get(PROP_MAC_ADDRESS);
	}

	@Property(displayName = "GPI Input", description = "Input of GPI", writable = false, type = PropertyType.PT_INTEGER)
	public Integer getExternalInput() {
		return Integer.parseInt(readerProperties.get(PROP_EXTERNAL_INPUT));
	}

	@Property(displayName = "Uptime", description = "Uptime of readerSession", writable = false, type = PropertyType.PT_INTEGER)
	public Integer getUptime() {
		return Integer.parseInt(readerProperties.get(PROP_UPTIME));
	}
}
