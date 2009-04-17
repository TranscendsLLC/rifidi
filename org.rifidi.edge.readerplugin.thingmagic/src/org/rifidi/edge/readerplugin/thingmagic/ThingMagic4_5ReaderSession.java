package org.rifidi.edge.readerplugin.thingmagic;

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


public class ThingMagic4_5ReaderSession extends AbstractIPReaderSession {

	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(ThingMagic4_5ReaderSession.class);
	
	private NotifierServiceWrapper notifierService;
	private String readerID;
	
	public static final char NEWLINE = '\n';
	public static final String COMMAND_TERMINATOR = ";";
	
	public static final String GET_VERSION_STRING = "select version from settings;\n";

	public ThingMagic4_5ReaderSession(String ID, String host, int port,
			int reconnectionInterval, int maxConAttempts,
			Destination destination, JmsTemplate template,
			NotifierServiceWrapper notifierService, String readerID) {
		super(ID, host, port, reconnectionInterval, maxConAttempts, destination,
				template);
		// TODO Auto-generated constructor stub
		
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
		_messageBuilder = Arrays.copyOf(_messageBuilder,
				_messageBuilder.length + 1);
		_messageBuilder[_messageBuilder.length - 1] = message;
		
		if ( (_messageBuilder[_messageBuilder.length - 1] == NEWLINE) && 
				(_messageBuilder[_messageBuilder.length - 2] == NEWLINE)){
			byte[] retVal = Arrays.copyOf(_messageBuilder,
					_messageBuilder.length - 2);
			
			_messageBuilder = new byte[0];
			
			return retVal;
		}
		return null;
	}

	@Override
	public boolean onConnect() throws IOException {
		// TODO Auto-generated method stub
		sendMessage(new ByteMessage(GET_VERSION_STRING.getBytes()));
		String versionString = new String(receiveMessage().message);
		logger.debug(versionString);
		
		//TODO get examples of other ThingMagic version strings.
		if (!versionString.contains("Tesla")) {
			logger.fatal("ReaderSession is not a thingmagic readerSession");
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
