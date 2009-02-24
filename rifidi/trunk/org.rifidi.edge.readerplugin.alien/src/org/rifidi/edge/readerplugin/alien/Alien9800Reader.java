/**
 * 
 */
package org.rifidi.edge.readerplugin.alien;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.commands.CommandState;
import org.rifidi.edge.core.readers.impl.AbstractReader;
import org.rifidi.edge.readerplugin.alien.commands.internal.AuthenticateCommand;
import org.rifidi.edge.readerplugin.alien.communication.ReadThread;
import org.rifidi.edge.readerplugin.alien.communication.WriteThread;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class Alien9800Reader extends AbstractReader {
	/** Logger for this class. */
	private static final Log logger = LogFactory.getLog(Alien9800Reader.class);
	/** IP address of the reader. */
	private String ipAddress;
	/** Port to connect to. */
	private Integer port;
	/** Username for the telnet interface. */
	private String username;
	/** Password for the telnet interface. */
	private String password;
	/** Time between two connection attempts. */
	private Long reconnectionInterval;
	/** Number of connection attempts before a connection goes into fail state. */
	private Integer maxNumConnectionAttempts;
	/** Socket through whcih we communicate with the reader. */
	private Socket socket;
	/** Stream for incoming data. */
	private InputStream inputStream;
	/** Stream for outgoing data. */
	private OutputStream outputStream;
	/** Each command needs to be terminated with a newline. */
	public static final String NEWLINE = "\n";
	/** Welcome string. */
	public static final String WELCOME = "Alien";
	/** Character that terminates a message from alien. */
	public static final char TERMINATION_CHAR = '\0';
	/**
	 * You can put this in front of a Alien command for terse output to come
	 * back to you, making things faster and easier to parse.
	 */
	public static final String PROMPT_SUPPRESS = "\1";

	/** Tag list command. */
	public static final String TAG_LIST = ('\1' + "get taglist\n");
	/** Tag list format command. */
	public static final String TAG_LIST_FORMAT = ('\1' + "set TagListFormat=Custom\n");
	/**	Set type of tags to query for. */
	public static final String TAG_TYPE_COMMAND = ('\1' + "set TagType=");
	/** Set antenna sequence */
	public static final String ANTENNA_SEQUENCE_COMMAND = ('\1' + "set AntennaSequence=");
	/** Tag list custom format command. */
	public static final String TAG_LIST_CUSTOM_FORMAT = ('\1' + "set TagListCustomFormat=%k|%T|%a\n");
	/** Get the timezone of the reader. */
	public static final String GET_TIME_ZONE = ('\1' + "get TimeZone\n");
	
	/**
	 * COMMANDS
	 */
	private static final String COMMAND_HEARTBEAT_ADDRESS = "heartbeataddress";
	private static final String COMMAND_ANTENNA_SEQUENCE = "antennasequence";
	private static final String COMMAND_MAX_ANTENNA = "maxantenna";
	private static final String COMMAND_PASSWORD= "password";
	private static final String COMMAND_READERNAME = "ReaderName";
	private static final String COMMAND_READERNUMBER = "ReaderNumber";
	private static final String COMMAND_READER_TYPE = "ReaderType";
	private static final String COMMAND_READER_VERSION = "ReaderVersion";
	private static final String COMMAND_RF_ATTENUATION = "RFAttenuation";
	private static final String COMMAND_EXTERNAL_INPUT= "ExternalInput";
	private static final String COMMAND_USERNAME = "username";
	private static final String COMMAND_UPTIME = "Uptime";
	private static final String COMMAND_TAG_TYPE= "tagtype";
	private static final String COMMAND_EXTERNAL_OUTPUT = "ExternalOutput";
	private static final String COMMAND_INVERT_EXTERNAL_INPUT = "InvertExternalInput";
	private static final String COMMAND_INVERT_EXTERNAL_OUTPUT= "InvertExternalOutput";
	private static final String COOMMAND_COMMAND_PORT= "CommandPort";
	private static final String COMMAND_DHCP = "DHCP";
	private static final String COMMAND_DNS= "DNS";
	private static final String COMMAND_GATEWAY= "Gateway";
	private static final String COMMAND_HEARTBEAT_COUNT= "HeartbeatCount";
	private static final String COMMAND_HEARTBEAT_PORT= "HeartbeatPort";
	private static final String COMMAND_HEARTBEAT_TIME = "HeartbeatTime";
	private static final String COMMAND_IPADDRESS= "IPAddress";
	private static final String COMMAND_MAC_ADDRESS = "MACAddress";
	private static final String COMMAND_NETMASK = "Netmask";
	private static final String COMMAND_NETWORK_TIMEOUT= "NetworkTimeout";
	private static final String COMMAND_TIME= "Time";
	private static final String COMMAND_TIME_SERVER= "TimeServer";
	private static final String COMMAND_TIME_ZONE= "TimeZone";
	
	/** Thread for reading from the socket. */
	private Thread readThread;
	/** Thread for writing to the socket. */
	private Thread writeThread;

	/**
	 * Constructor.
	 * 
	 * @param ipAddress
	 * @param port
	 * @param username
	 * @param password
	 * @param reconnectionInterval
	 * @param maxNumConnectionAttempts
	 */
	public Alien9800Reader(String ipAddress, Integer port, String username,
			String password, Long reconnectionInterval,
			Integer maxNumConnectionAttempts) {
		super();
		this.ipAddress = ipAddress;
		this.port = port;
		this.username = username;
		this.password = password;
		this.reconnectionInterval = reconnectionInterval;
		this.maxNumConnectionAttempts = maxNumConnectionAttempts;
	}

	/**
	 * Open the connection.
	 * 
	 * @throws IOException
	 */
	protected void connect() throws IOException {
		// try to open the socket
		for (int connCount = 0; connCount < maxNumConnectionAttempts; connCount++) {
			try {
				socket = new Socket(ipAddress, port);
				inputStream = socket.getInputStream();
				outputStream = socket.getOutputStream();
				break;
			} catch (IOException e) {
				logger.warn("Unable to connect to reader on try nr "
						+ connCount + " " + e);
			}
			// sleep between to connection attempts
			try {
				Thread.sleep(reconnectionInterval);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}
		}
		// no socket, we are screwed
		if (socket == null) {
			throw new IOException("Unable to reach reader.");
		}
		readThread = new Thread(new ReadThread(inputStream, readQueue));
		writeThread = new Thread(new WriteThread(outputStream, writeQueue));
		readThread.start();
		writeThread.start();

		// try to authenticate
		try {
			AuthenticateCommand command=new AuthenticateCommand(username, password);
			command.setReader(this);
			Future<CommandState> future = execute(command);
			CommandState state = future.get();
			// if we lost the connection try to reconnect
			if (CommandState.LOSTCONNECTION.equals(state)) {
				connect();
			} else if (!CommandState.DONE.equals(state)) {
				// probably wrong password
				logger.warn("Unable to authenticate: " + state);
				throw new IOException("Unable to connect to reader.");
			}
		} catch (InterruptedException e) {
			writeThread.interrupt();
			readThread.interrupt();
			Thread.currentThread().interrupt();
		} catch (ExecutionException e) {
			logger.error("Unable to execute command: " + e);
			throw new IOException("Unable to authenticate: " + e);
		}
	}
	
	protected void cleanup(){
		try {
			this.socket.close();
		} catch (IOException e) {
		}
	}
}
