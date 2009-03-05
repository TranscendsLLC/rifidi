package org.rifidi.edge.readerplugin.alien.commandobject;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.readers.ByteMessage;
import org.rifidi.edge.readerplugin.alien.AbstractAlien9800Command;
import org.rifidi.edge.readerplugin.alien.Alien9800ReaderSession;

/**
 * This is an Command Object use to formulate individual request/response
 * interactions with an Alien ReaderSession. It provides default behavior for
 * getting and setting properties on a command where the command is of the form
 * 
 * get commandname\n
 * 
 * where the response is of the form
 * 
 * commandname=value\n
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AlienCommandObject {

	/** The command to be sent to the Alien ReaderSession */
	private String command;
	/** The readerSession to send the command to */
	private Alien9800ReaderSession readerSession;
	/** A logger for this class */
	private static final Log logger = LogFactory
			.getLog(AbstractAlien9800Command.class);

	/**
	 * Construct a command object.
	 * 
	 * @param command
	 *            A command to be send to the Alien readerSession (see the
	 *            static command strings in AlienReader)
	 * @param readerSession
	 *            A live readerSession to send the command to
	 */
	public AlienCommandObject(String command,
			Alien9800ReaderSession readerSession) {
		this.command = command;
		this.readerSession = readerSession;
	}

	/**
	 * Execute A get
	 * 
	 * 
	 * 
	 * @return the value of the property on the alien readerSession
	 * @throws IOException
	 */
	public String executeGet() throws IOException, AlienException {
		String message = Alien9800ReaderSession.PROMPT_SUPPRESS + "get "
				+ command + Alien9800ReaderSession.NEWLINE;

		readerSession.sendMessage(new ByteMessage(message.getBytes()));

		ByteMessage incomingMessage = readerSession.receiveMessage();

		String incoming = new String(incomingMessage.message);
		if (incoming.contains("=")) {
			String[] splitString = incoming.split("=");
			return splitString[1].trim();
		} else if (incoming.contains("Error")) {
			logger.warn("Error while Getting Property: " + incoming.trim());
			throw new AlienException(incoming.trim());
		} else {
			logger.warn("Something is wrong with the command: "
					+ incoming.trim());
			throw new AlienException(incoming.trim());
		}
	}

	/**
	 * Execute a set
	 * 
	 * For commands of the form
	 * 
	 * set commandname = value\n
	 * 
	 * where the response is of the form
	 * 
	 * commandname=value\n
	 * 
	 * @param value
	 *            the value to set
	 * 
	 * @return the value the alien readerSession set it to
	 * @throws IOException
	 */
	public String executeSet(String value) throws IOException, AlienException {
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
			logger.warn("Error while Setting Property: " + incoming.trim());
			throw new AlienException(incoming.trim());
		} else {
			logger.warn("Something is wrong with the command: " + incoming);
			throw new AlienException(incoming.trim());
		}
	}

}
