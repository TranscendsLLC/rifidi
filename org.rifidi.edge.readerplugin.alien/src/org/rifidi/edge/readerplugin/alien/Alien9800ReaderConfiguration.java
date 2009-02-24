/**
 * 
 */
package org.rifidi.edge.readerplugin.alien;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.configuration.annotations.JMXMBean;
import org.rifidi.configuration.annotations.Property;
import org.rifidi.edge.core.exceptions.NoReaderAvailableException;
import org.rifidi.edge.core.readers.AbstractReaderConfiguration;

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
	/** ID of the service. */
	private String id;
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
	
	private String heartbeat_address = "127.0.0.1";
	private String antenna_sequence = "0";
	private int max_antenna = 4;
	private String readername;
	private String readernumber;
	private String reader_type;
	private String reader_version;
	private int rf_attenuation=0;
	private int external_input= 0;
	private long uptime;
	private int tag_type= 31;
	private int external_output=0;
	private String invert_external_input = "off";
	private String invert_external_output= "off";
	private int command_port;
	private String DHCP;
	private String DNS;
	private String gateway;
	private int heartbeat_count= 0;
	private int heartbeat_port= 0;
	private int heartbeat_time = 0;
	/**ip address of reader*/
	private String ipaddress;
	private String mac_address;
	private String netmask;
	private String network_timeout;
	private String time;
	private String time_server;
	private String time_zone;
	

	/**
	 * Constructor.
	 */
	public Alien9800ReaderConfiguration() {
		logger.debug("New instance of Alien 9800 Reader config created.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.ReaderConfiguration#aquireReader()
	 */
	@Override
	public synchronized Alien9800Reader aquireReader()
			throws NoReaderAvailableException {
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
		return reader;
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
	 * org.rifidi.edge.core.ReaderConfiguration#releaseReader(java.lang.Object
	 * )
	 */
	@Override
	public void releaseReader(Object reader) {
		if (reader.equals(this.reader)) {
			((Alien9800Reader)reader).cleanup();
			this.reader = null;
			return;
		}
		logger.warn("Released reader doesn't matched aquired reader: " + reader
				+ " " + this.reader);
	}

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
		this.maxNumConnectionAttempts = Integer.parseInt(maxNumConnectionAttempts);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.configuration.RifidiService#getID()
	 */
	@Override
	public String getID() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.configuration.RifidiService#setID(java.lang.String)
	 */
	@Override
	public void setID(String id) {
		this.id = id;
	}

}
