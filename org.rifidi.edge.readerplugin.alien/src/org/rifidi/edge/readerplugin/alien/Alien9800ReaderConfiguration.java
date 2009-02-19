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
import org.rifidi.edge.newcore.exceptions.NoReaderAvailableException;
import org.rifidi.edge.newcore.readers.ReaderConfiguration;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
@JMXMBean
public class Alien9800ReaderConfiguration implements
		ReaderConfiguration<Alien9800Reader> {
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

	/**
	 * Constructor.
	 */
	public Alien9800ReaderConfiguration() {
		logger.debug("New instance of Alien 9800 Reader config created.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.newcore.ReaderConfiguration#aquireReader()
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
	 * @see org.rifidi.edge.newcore.ReaderConfiguration#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.newcore.ReaderConfiguration#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.newcore.ReaderConfiguration#releaseReader(java.lang.Object
	 * )
	 */
	@Override
	public void releaseReader(Object reader) {
		if (reader.equals(this.reader)) {
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
	public Integer getPort() {
		return port;
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
	@Property(name = "Maximum Connection Attempts", description = "Number of times to try to connect to the reader before the connection is marked as failed.", writable = true)
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
