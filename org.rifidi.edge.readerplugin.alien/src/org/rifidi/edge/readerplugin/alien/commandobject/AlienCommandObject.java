package org.rifidi.edge.readerplugin.alien.commandobject;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.readers.Reader;
import org.rifidi.edge.readerplugin.alien.AbstractAlien9800Command;
import org.rifidi.edge.readerplugin.alien.Alien9800Reader;

/**
 * This is an Command Object use to formulate individual request/response
 * interactions with an Alien Reader. It provides default behavior for getting
 * and setting properties on a command, but the default behavior should be
 * overriden if need be (for example, if the parsing of a response is different
 * than what this provides)
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AlienCommandObject {

	/** The command to be sent to the Alien Reader */
	private String command;
	/** The reader to send the command to */
	private Reader reader;
	/** A logger for this class */
	private static final Log logger = LogFactory
			.getLog(AbstractAlien9800Command.class);

	/**
	 * Construct a command object.
	 * 
	 * @param command
	 *            A command to be send to the Alien reader (see the static
	 *            command strings in AlienReader)
	 * @param reader
	 *            A live reader to send the command to
	 */
	public AlienCommandObject(String command, Reader reader) {
		this.command = command;
		this.reader = reader;
	}

	/**
	 * Execute A get
	 * 
	 * @return the value of the property on the alien reader or "Error" if there
	 *         was a problem
	 * @throws IOException
	 */
	public String executeGet() throws IOException {
		reader.sendMessage(Alien9800Reader.PROMPT_SUPPRESS + "get " + command
				+ Alien9800Reader.NEWLINE);
		String incoming = (String) reader.receiveMessage();
		if (incoming.contains("=")) {
			String[] splitString = incoming.split("=");
			return splitString[1].trim();
		} else if (incoming.contains("Error")) {
			logger.warn("Error while Setting Property: " + incoming.trim());
			return "Error";
		} else {
			logger.warn("Something is wrong with the command: " + incoming);
			return "";
		}
	}

	/**
	 * Execute a set
	 * 
	 * @param value
	 *            the value to set
	 * 
	 * @return the value the alien reader set it to, or "Error" if there was a
	 *         problem
	 * @throws IOException
	 */
	public String executeSet(String value) throws IOException {
		assert(value!=null);
		reader.sendMessage(Alien9800Reader.PROMPT_SUPPRESS + "set " + command
				+ "=" + value + Alien9800Reader.NEWLINE);
		String incoming = (String) reader.receiveMessage();
		if (incoming.contains("=")) {
			String[] splitString = incoming.split("=");
			return splitString[1].trim();
		} else if (incoming.contains("Error")) {
			logger.warn("Error while Setting Property: " + incoming.trim());
			return "Error";
		} else {
			logger.warn("Something is wrong with the command: " + incoming);
			return "";
		}
	}

}
