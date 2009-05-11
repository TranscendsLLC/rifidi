/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.commandobject;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.readers.ByteMessage;
import org.rifidi.edge.readerplugin.alien.Alien9800ReaderSession;

/**
 * A command object that will set an property on the Alien Reader
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AlienSetCommandObject extends AlienCommandObject {

	/** The value to set */
	private String value;
	/** The logger for this class */
	private Log logger = LogFactory.getLog(AlienSetCommandObject.class);

	/**
	 * Constructor
	 * 
	 * @param command
	 *            The command to execute
	 * @param readerSession
	 *            The session to execute the command on
	 * @param value
	 *            The value to set
	 */
	public AlienSetCommandObject(String command, String value) {
		super(command);
		this.value = value;
	}

	/**
	 * Constructor
	 * 
	 * @param command
	 *            The command to execute
	 * @param value
	 *            The value to set
	 * @param session
	 *            The session to execute the command on
	 */
	public AlienSetCommandObject(String command, String value,
			Alien9800ReaderSession session) {
		super(command);
		this.value = value;
		super.setSession(session);
	}

	@Override
	public String execute() throws IOException, AlienException {
		assert (value != null);
		String message = Alien9800ReaderSession.PROMPT_SUPPRESS + "set "
				+ command + "=" + value + Alien9800ReaderSession.NEWLINE;
		readerSession.sendMessage(new ByteMessage(message.getBytes()));
		ByteMessage incomingMessage = readerSession.receiveMessage();
		String incoming = new String(incomingMessage.message);
		if (incoming.contains("=")) {
			String[] splitString = incoming.split("=");
			return splitString[1].trim();
		} else if (incoming.contains("Error")) {
			throw new AlienException(incoming.trim());
		} else {
			throw new AlienException(incoming.trim());
		}

	}

}
