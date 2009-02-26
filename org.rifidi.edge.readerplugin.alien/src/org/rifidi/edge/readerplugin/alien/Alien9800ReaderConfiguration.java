/**
 * 
 */
package org.rifidi.edge.readerplugin.alien;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.configuration.annotations.JMXMBean;
import org.rifidi.configuration.annotations.Operation;
import org.rifidi.configuration.annotations.Property;
import org.rifidi.configuration.annotations.PropertyType;
import org.rifidi.edge.core.commands.CommandState;
import org.rifidi.edge.core.exceptions.NoReaderAvailableException;
import org.rifidi.edge.core.readers.AbstractReaderConfiguration;
import org.rifidi.edge.readerplugin.alien.commands.internal.AlienGetReaderPropertiesCommand;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
@JMXMBean
public class Alien9800ReaderConfiguration extends
		AbstractReaderConfiguration<Alien9800Reader> {
	/** Logger for this class. */
	private Log logger = LogFactory.getLog(Alien9800ReaderConfiguration.class);
	/** Instance of the reader that has been handed out. */
	private Alien9800Reader reader;
	/** Description of the reader. */
	private static final String description = "The Alien 9800 is an IP based RFID Reader using a telnet interface.";
	/** Name of the reader. */
	private static final String name = "Alien9800";
	/**
	 * A semaphore to make sure a two threads are attempting to aquire a reader
	 * at the same time
	 */
	private Semaphore readerSemaphore = new Semaphore(1);

	/***
	 * READER PROPERTIES - CONNECTION INFO
	 */
	private HashMap<String, Object> readerProperties;
	/** IP address of the reader. */
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
	/** ip address of reader */
	private String ipaddress;
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
	/** The unique number of the reader */
	public static final String PROP_READER_NUMBER = "readerNumber";

	/**
	 * READER PROPERTIES - READ ONLY, INITIALZIED BY AQUIRE READER PROPERTIES
	 */
	/** MAC Address of the reader */
	public static final String PROP_MAC_ADDRESS = "macAddress";
	/** Maximum number of antennas supported */
	public static final String PROP_MAX_ANTENNA = "maxAntenna";
	/** The type of the alien reader */
	public static final String PROP_READER_TYPE = "readerType";
	/** The version of the reader */
	public static final String PROP_READER_VERSION = "readerVersion";
	/** GPO value */
	public static final String PROP_EXTERNAL_INPUT = "externalinput";
	/** Uptime of the reader */
	public static final String PROP_UPTIME = "uptime";

	/**
	 * Constructor.
	 */
	public Alien9800ReaderConfiguration() {
		readerProperties = new HashMap<String, Object>();
		readerProperties.put(PROP_READER_NUMBER, "0");
		readerProperties.put(PROP_READER_VERSION, "Unavailable");
		readerProperties.put(PROP_READER_TYPE, "Unavailable");
		readerProperties.put(PROP_MAC_ADDRESS, "Unavailable");
		readerProperties.put(PROP_MAX_ANTENNA, 0);
		readerProperties.put(PROP_EXTERNAL_INPUT, 0);
		readerProperties.put(PROP_UPTIME, 0);
		readerProperties.put(PROP_EXTERNAL_OUTPUT, 0);
		readerProperties.put(PROP_RF_ATTENUATION, 0);
		logger.debug("New instance of Alien 9800 Reader config created.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.ReaderConfiguration#aquireReader()
	 */
	@Override
	public Alien9800Reader aquireReader() throws NoReaderAvailableException {
		try {
			readerSemaphore.acquire();
			createReader();
			return reader;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new NoReaderAvailableException();
		} finally {
			readerSemaphore.release();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.readers.AbstractReaderConfiguration#
	 * aquireReaderProperties()
	 */
	@Override
	@Operation(description = "Get the properties stored on the reader")
	public void configureReader() {
		boolean readerAquired = readerSemaphore.tryAcquire();
		if (readerAquired != true) {
			logger.warn("Cannot aquire Reader Properties");
			return;
		}
		try {
			createReader();
			HashMap<String, Object> attrList = new HashMap<String, Object>();
			attrList.putAll(readerProperties);
			AlienGetReaderPropertiesCommand command = new AlienGetReaderPropertiesCommand(
					attrList);
			command.setReader(reader);
			Future<CommandState> f = reader.execute(command);
			CommandState commandState = f.get();
			if (commandState == CommandState.DONE) {
				this.readerProperties.putAll(attrList);
			}
		} catch (InterruptedException e) {
			logger.warn("Exception while aquiring reader properties ", e);
			Thread.currentThread().interrupt();
		} catch (ExecutionException e) {
			logger.warn("Exception while aquiring reader properties ", e);
		} catch (NoReaderAvailableException e) {
			logger.error("No Reader Available: ", e);
		} finally {
			releaseReader(reader);
			readerSemaphore.release();
		}
	}

	/**
	 * This private method actually does the work of creating a new reader and
	 * connecting
	 * 
	 * @throws NoReaderAvailableException
	 *             If a reader is not available at this time
	 */
	private void createReader() throws NoReaderAvailableException {
		if (reader != null) {
			throw new NoReaderAvailableException(
					"This configuration only supports one reader at a time.");
		}
		reader = new Alien9800Reader(ipAddress, port, username, password,
				reconnectionInterval, maxNumConnectionAttempts);
		try {
			reader.connect();

		} catch (UnknownHostException e) {
			throw new NoReaderAvailableException(e);
		} catch (IOException e) {
			throw new NoReaderAvailableException(e);
		}
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
	 * @see
	 * org.rifidi.edge.core.ReaderConfiguration#releaseReader(java.lang.Object )
	 */
	@Override
	public void releaseReader(Object reader) {
		if (reader.equals(this.reader)) {
			((Alien9800Reader) reader).cleanup();
			this.reader = null;
			return;
		}
		logger.warn("Released reader doesn't matched aquired reader: " + reader
				+ " " + this.reader);
	}

	/**
	 * JMX PROPERTY GETTER/SETTERS
	 */

	/**
	 * @return the ipAddress
	 */
	@Property(displayName = "IP Address", description = "Address of the reader.", writable = true)
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
	@Property(displayName = "Port", description = "Port of the reader.", writable = true, type = PropertyType.PT_INTEGER)
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
	@Property(displayName = "Username", description = "Username for logging into the reader.", writable = true)
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
	@Property(displayName = "Password", description = "Password for logging into the reader.", writable = true)
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
	@Property(displayName = "Maximum Connection Attempts", description = "Number of times to try to connect to the reader before the connection is marked as failed.", writable = true, type = PropertyType.PT_INTEGER)
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
		//AlienConfigureReader may put the value in the hashmap as a string
		Object val = readerProperties.get(PROP_EXTERNAL_OUTPUT);
		if(val instanceof String){
			readerProperties.put(PROP_EXTERNAL_OUTPUT, Integer.parseInt((String)val));
		}
		return (Integer) readerProperties.get(PROP_EXTERNAL_OUTPUT);
	}

	public void setExternalOutput(Integer externalOutput) {
		if (externalOutput >= 0 && externalOutput <= 255) {
			readerProperties.put(PROP_EXTERNAL_OUTPUT, externalOutput);
			return;
		}
		logger.warn("ExternalOutput must be an"
				+ " integer between 0 and 255, but was " + externalOutput);
	}

	@Property(displayName = "RF Attenuation", description = "RF Attenuation", writable = true, type = PropertyType.PT_INTEGER)
	public Integer getRFAttenuation() {
		//AlienConfigureReader may put the value in the hashmap as a string
		Object val = readerProperties.get(PROP_RF_ATTENUATION);
		if(val instanceof String){
			readerProperties.put(PROP_RF_ATTENUATION, Integer.parseInt((String)val));
		}
		return (Integer) readerProperties.get(PROP_RF_ATTENUATION);
	}

	public void setRFAttenuation(Integer rfAttenuation) {
		readerProperties.put(PROP_RF_ATTENUATION, rfAttenuation);
	}

	@Property(displayName = "Reader Version", description = "Version Number of the reader", writable = false)
	public String getReaderVersion() {
		return (String) readerProperties.get(PROP_READER_VERSION);
	}

	@Property(displayName = "Reader Type", description = "Type of Reader", writable = false)
	public String getReaderType() {
		return (String)readerProperties.get(PROP_READER_TYPE);
	}

	@Property(displayName = "Max Antennas", description = "Maximum number of antennas", writable = false, type=PropertyType.PT_INTEGER)
	public Integer getMaxAntennas() {
		//AlienConfigureReader may put the value in the hashmap as a string
		Object val = readerProperties.get(PROP_MAX_ANTENNA);
		if(val instanceof String){
			readerProperties.put(PROP_MAX_ANTENNA, Integer.parseInt((String)val));
		}
		return (Integer)readerProperties.get(PROP_MAX_ANTENNA);
	}

	@Property(displayName = "MAC Address", description = "MAC Address of reader", writable = false)
	public String getMACAddress() {
		return (String)readerProperties.get(PROP_MAC_ADDRESS);
	}

	@Property(displayName = "GPI Input", description = "Input of GPI", writable = false, type=PropertyType.PT_INTEGER)
	public Integer getExternalInput() {
		//AlienConfigureReader may put the value in the hashmap as a string
		Object val = readerProperties.get(PROP_EXTERNAL_INPUT);
		if(val instanceof String){
			readerProperties.put(PROP_EXTERNAL_INPUT, Integer.parseInt((String)val));
		}
		return (Integer)readerProperties.get(PROP_EXTERNAL_INPUT);
	}

	@Property(displayName = "Uptime", description = "Uptime of reader", writable = false, type=PropertyType.PT_INTEGER)
	public Integer getUptime() {
		//AlienConfigureReader may put the value in the hashmap as a string
		Object val = readerProperties.get(PROP_UPTIME);
		if(val instanceof String){
			readerProperties.put(PROP_UPTIME, Integer.parseInt((String)val));
		}
		return (Integer)readerProperties.get(PROP_UPTIME);
	}
}
