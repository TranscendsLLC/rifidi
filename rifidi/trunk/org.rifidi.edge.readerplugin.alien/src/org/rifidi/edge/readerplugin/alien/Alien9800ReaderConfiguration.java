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
	private HashMap<String, String> readerProperties;
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
		readerProperties = new HashMap<String, String>();
		readerProperties.put(PROP_READER_NUMBER, "0");
		readerProperties.put(PROP_READER_VERSION, "Unavailable");
		readerProperties.put(PROP_READER_TYPE, "Unavailable");
		readerProperties.put(PROP_MAC_ADDRESS, "Unavailable");
		readerProperties.put(PROP_MAX_ANTENNA, "Unavailable");
		readerProperties.put(PROP_EXTERNAL_INPUT, "Unavailable");
		readerProperties.put(PROP_UPTIME, "Unavailable");
		readerProperties.put(PROP_EXTERNAL_OUTPUT, "0");
		readerProperties.put(PROP_RF_ATTENUATION, "0");
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
			HashMap<String, String> attrList = new HashMap<String, String>();
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
	@Property(name = "IP Address", description = "Address of the reader.", writable = true)
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
	@Property(name = "Port", description = "Port of the reader.", writable = true)
	public String getPort() {
		return Integer.toString(port);
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(String port) {
		this.port = Integer.parseInt(port);
	}

	/**
	 * @return the username
	 */
	@Property(name = "Username", description = "Username for logging into the reader.", writable = true)
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
	@Property(name = "Password", description = "Password for logging into the reader.", writable = true)
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
	@Property(name = "Reconnection Interval", description = "Time between two connection attempts (ms).", writable = true)
	public String getReconnectionInterval() {
		return Long.toString(reconnectionInterval);
	}

	/**
	 * @param reconnectionInterval
	 *            the reconnectionInterval to set
	 */
	public void setReconnectionInterval(String reconnectionInterval) {
		this.reconnectionInterval = Long.parseLong(reconnectionInterval);
	}

	/**
	 * @return the maxNumConnectionAttempts
	 */
	@Property(name = "Maximum Connection Attempts", description = "Number of times to try to connect to the reader before the connection is marked as failed.", writable = true)
	public String getMaxNumConnectionAttempts() {
		return Long.toString(maxNumConnectionAttempts);
	}

	/**
	 * @param maxNumConnectionAttempts
	 *            the maxNumConnectionAttempts to set
	 */
	public void setMaxNumConnectionAttempts(String maxNumConnectionAttempts) {
		this.maxNumConnectionAttempts = Integer
				.parseInt(maxNumConnectionAttempts);
	}

	@Property(name = "GPO Output", description = "Ouput of GPO", writable = true)
	public String getExternalOutput() {
		return readerProperties.get(PROP_EXTERNAL_OUTPUT);
	}

	public void setExternalOutput(String externalOutput) {
		int i = Integer.parseInt(externalOutput);
		if (i >= 0 && i <= 255) {
			readerProperties.put(PROP_EXTERNAL_OUTPUT, externalOutput);
			return;
		}
		logger.warn("ExternalOutput must be an"
				+ " integer between 0 and 255, but was " + externalOutput);
	}

	@Property(name = "RF Attenuation", description = "RF Attenuation", writable = true)
	public String getRFAttenuation() {
		return readerProperties.get(PROP_RF_ATTENUATION);
	}

	public void setRFAttenuation(String rfAttenuation) {
		readerProperties.put(PROP_RF_ATTENUATION, rfAttenuation);
	}

	@Property(name = "Reader Version", description = "Version Number of the reader", writable = false)
	public String getReaderVersion() {
		return readerProperties.get(PROP_READER_VERSION);
	}

	@Property(name = "Reader Type", description = "Type of Reader", writable = false)
	public String getReaderType() {
		return readerProperties.get(PROP_READER_TYPE);
	}

	@Property(name = "Max Antennas", description = "Maximum number of antennas", writable = false)
	public String getMaxAntennas() {
		return readerProperties.get(PROP_MAX_ANTENNA);
	}

	@Property(name = "MAC Address", description = "MAC Address of reader", writable = false)
	public String getMACAddress() {
		return readerProperties.get(PROP_MAC_ADDRESS);
	}

	@Property(name = "GPI Input", description = "Input of GPI", writable = false)
	public String getExternalInput() {
		return readerProperties.get(PROP_EXTERNAL_INPUT);
	}

	@Property(name = "Uptime", description = "Uptime of reader", writable = false)
	public String getUptime() {
		return readerProperties.get(PROP_UPTIME);
	}
}
