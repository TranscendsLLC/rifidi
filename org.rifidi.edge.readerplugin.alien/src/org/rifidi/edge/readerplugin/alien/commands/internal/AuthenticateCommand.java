/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.commands.internal;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.api.readerplugin.messageQueue.MessageQueue;
import org.rifidi.edge.newcore.commands.Command;
import org.rifidi.edge.newcore.commands.CommandState;
import org.rifidi.edge.readerplugin.alien.Alien9800Reader;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class AuthenticateCommand extends Command {
	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(AuthenticateCommand.class);
	/** Username for connecting. */
	private String username;
	/** Password for connecting. */
	private String password;

	/**
	 * Constructor.
	 * 
	 * @param username
	 * @param password
	 */
	public AuthenticateCommand(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.newcore.Command#call()
	 */
	@Override
	public CommandState call() throws Exception {
		// TODO: add some validation
		logger.debug("getting the welcome response");
		String welcome = (String) getReader().receiveMessage();
		logger.debug(welcome);

		if (welcome == null || !welcome.contains(Alien9800Reader.WELCOME)) {
			logger.fatal("Reader is not an alien reader: " + welcome);
			throw new IOException("Reader is not an alien reader");
		} else if (welcome.toLowerCase().contains("busy")) {
			logger.error("Reader is busy: " + welcome);
			throw new IOException("Reader is busy: " + welcome);
		} else {
			logger.debug("Reader is an alien.  Hoo-ray!");
		}

		logger.debug("sending username");
		getReader().sendMessage(Alien9800Reader.PROMPT_SUPPRESS + username
				+ Alien9800Reader.NEWLINE);
		logger.debug("getting the username response");
		getReader().receiveMessage();
		logger.debug("sending the password. ");
		getReader().sendMessage(Alien9800Reader.PROMPT_SUPPRESS + password
				+ Alien9800Reader.NEWLINE);
		logger.debug("recieving the password response");
		getReader().receiveMessage();
		return CommandState.DONE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.newcore.Command#stop()
	 */
	@Override
	public void stop() {
		// this command is unstoppable!!!!!
		// TODO: not really, kill the blocking calls
	}

}
