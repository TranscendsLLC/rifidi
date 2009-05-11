/**
 * 
 */
package org.rifidi.edge.readerplugin.alien;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.jms.Destination;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.api.SessionStatus;
import org.rifidi.edge.core.commands.Command;
import org.rifidi.edge.core.notifications.NotifierService;
import org.rifidi.edge.core.readers.ByteMessage;
import org.rifidi.edge.core.readers.impl.AbstractIPReaderSession;
import org.springframework.jms.core.JmsTemplate;

/**
 * A session that connects to an Alien9800Reader
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class Alien9800ReaderSession extends AbstractIPReaderSession {
	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(Alien9800ReaderSession.class);
	/** Username for connecting to the reader. */
	private String username;
	/** Password for connnecting to the reader. */
	private String password;
	/** Each command needs to be terminated with a newline. */
	public static final String NEWLINE = "\n";
	/** Welcome string. */
	public static final String WELCOME = "Alien";
	/** Character that terminates a message from alien. */
	public static final char TERMINATION_CHAR = '\0';
	/** Service used to send out notifications */
	private NotifierServiceWrapper notifierService;
	/** The ID of the reader this session belongs to */
	private String readerID;

	/**
	 * You can put this in front of a Alien command for terse output to come
	 * back to you, making things faster and easier to parse.
	 */
	public static final String PROMPT_SUPPRESS = "\1";

	/** Set antenna sequence */
	public static final String ANTENNA_SEQUENCE_COMMAND = ('\1' + "set AntennaSequence=");

	/**
	 * COMMANDS
	 */
	public static final String COMMAND_HEARTBEAT_ADDRESS = "heartbeataddress";
	public static final String COMMAND_ANTENNA_SEQUENCE = "antennasequence";
	public static final String COMMAND_MAX_ANTENNA = "maxantenna";
	public static final String COMMAND_PASSWORD = "password";
	public static final String COMMAND_READERNAME = "ReaderName";
	public static final String COMMAND_READERNUMBER = "ReaderNumber";
	public static final String COMMAND_READER_TYPE = "ReaderType";
	public static final String COMMAND_READER_VERSION = "ReaderVersion";
	public static final String COMMAND_RF_ATTENUATION = "RFAttenuation";
	public static final String COMMAND_EXTERNAL_INPUT = "ExternalInput";
	public static final String COMMAND_USERNAME = "username";
	public static final String COMMAND_UPTIME = "Uptime";
	public static final String COMMAND_TAG_LIST = "taglist";
	public static final String COMMAND_TAG_TYPE = "tagtype";
	public static final String COMMAND_TAG_LIST_FORMAT = "TagListFormat";
	public static final String COMMAND_TAG_LIST_CUSTOM_FORMAT = "TagListCustomFormat";
	public static final String COMMAND_EXTERNAL_OUTPUT = "ExternalOutput";
	public static final String COMMAND_INVERT_EXTERNAL_INPUT = "InvertExternalInput";
	public static final String COMMAND_INVERT_EXTERNAL_OUTPUT = "InvertExternalOutput";
	public static final String COOMMAND_COMMAND_PORT = "CommandPort";
	public static final String COMMAND_DHCP = "DHCP";
	public static final String COMMAND_DNS = "DNS";
	public static final String COMMAND_GATEWAY = "Gateway";
	public static final String COMMAND_HEARTBEAT_COUNT = "HeartbeatCount";
	public static final String COMMAND_HEARTBEAT_PORT = "HeartbeatPort";
	public static final String COMMAND_HEARTBEAT_TIME = "HeartbeatTime";
	public static final String COMMAND_IPADDRESS = "IPAddress";
	public static final String COMMAND_MAC_ADDRESS = "MACAddress";
	public static final String COMMAND_NETMASK = "Netmask";
	public static final String COMMAND_NETWORK_TIMEOUT = "NetworkTimeout";
	public static final String COMMAND_TIME = "Time";
	public static final String COMMAND_TIME_SERVER = "TimeServer";
	public static final String COMMAND_TIME_ZONE = "TimeZone";

	/**
	 * 
	 * Constructor
	 * 
	 * @param id
	 *            The ID of the session
	 * @param host
	 *            The IP to connect to
	 * @param port
	 *            The port to connect to
	 * @param reconnectionInterval
	 *            The wait time between reconnect attempts
	 * @param maxConAttempts
	 *            The maximum number of times to try to connect
	 * @param username
	 *            The Alien username
	 * @param password
	 *            The Alien password
	 * @param destination
	 *            The JMS destination for tags
	 * @param template
	 *            The JSM template for tags
	 * @param notifierService
	 *            The service for sending client notifications
	 * @param readerID
	 *            The ID of the reader that created this session
	 */
	public Alien9800ReaderSession(String id, String host, int port,
			int reconnectionInterval, int maxConAttempts, String username,
			String password, Destination destination, JmsTemplate template,
			NotifierServiceWrapper notifierService, String readerID) {
		super(id, host, port, reconnectionInterval, maxConAttempts,
				destination, template);
		this.username = username;
		this.password = password;
		this.notifierService = notifierService;
		this.readerID = readerID;
	}

	/** The message currently being processed. */
	private byte[] _messageBuilder = new byte[0];

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readers.impl.AbstractIPReaderSession#isMessage(byte)
	 */
	@Override
	public byte[] isMessage(byte message) {
		if (TERMINATION_CHAR == message) {
			byte[] ret = _messageBuilder;
			_messageBuilder = new byte[0];
			return ret;
		}
		_messageBuilder = Arrays.copyOf(_messageBuilder,
				_messageBuilder.length + 1);
		_messageBuilder[_messageBuilder.length - 1] = message;
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readers.impl.AbstractIPReaderSession#onConnect()
	 */
	@Override
	public boolean onConnect() throws IOException {
		logger.debug("getting the welcome response");
		String welcome = new String(receiveMessage().message);
		logger.debug("welcome message: " + welcome);

		if (welcome == null
				|| !welcome.contains(Alien9800ReaderSession.WELCOME)) {
			logger.fatal("ReaderSession is not an alien readerSession: "
					+ welcome);
			return false;
		} else if (welcome.toLowerCase().contains("busy")) {
			logger.error("ReaderSession is busy: " + welcome);
			return false;
		} else {
			logger.debug("ReaderSession is an alien.  Hoo-ray!");
		}

		logger.debug("sending username");
		sendMessage(new ByteMessage((Alien9800ReaderSession.PROMPT_SUPPRESS
				+ username + Alien9800ReaderSession.NEWLINE).getBytes()));
		logger.debug("getting the username response");
		receiveMessage();
		logger.debug("sending the password. ");
		sendMessage(new ByteMessage((Alien9800ReaderSession.PROMPT_SUPPRESS
				+ password + Alien9800ReaderSession.NEWLINE).getBytes()));
		logger.debug("recieving the password response");
		String authMessage = new String(receiveMessage().message);
		if (authMessage.contains("Invalid")) {
			logger.warn("Incorrect Password");
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readers.impl.AbstractReaderSession#setStatus(org
	 * .rifidi.edge.core.api.SessionStatus)
	 */
	@Override
	protected synchronized void setStatus(SessionStatus status) {
		super.setStatus(status);

		// TODO: Remove this once we have aspectJ
		NotifierService service = notifierService.getNotifierService();
		if (service != null) {
			service.sessionStatusChanged(this.readerID, this.getID(), status);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readers.impl.AbstractReaderSession#killComand(java
	 * .lang.Integer)
	 */
	@Override
	public void killComand(Integer id) {
		super.killComand(id);

		// TODO: Remove this once we have aspectJ
		NotifierService service = notifierService.getNotifierService();
		if (service != null) {
			service.jobDeleted(this.readerID, this.getID(), id);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readers.impl.AbstractReaderSession#submit(org.rifidi
	 * .edge.core.commands.Command, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public Integer submit(Command command, long interval, TimeUnit unit) {
		Integer retVal = super.submit(command, interval, unit);

		// TODO: Remove this once we have aspectJ
		try {
			NotifierService service = notifierService.getNotifierService();
			if (service != null) {
				service.jobSubmitted(this.readerID, this.getID(), retVal,
						command.getCommandID());
			}
		} catch (Exception e) {
			// make sure the notification doesn't cause this method to exit
			// under any circumstances
		}
		return retVal;
	}

}
