/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.commands.internal;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.readerplugin.alien.AbstractAlien9800Command;
import org.rifidi.edge.readerplugin.alien.Alien9800ReaderSession;
import org.rifidi.edge.readerplugin.alien.commandobject.AlienCommandObjectWrapper;
import org.rifidi.edge.readerplugin.alien.commandobject.AlienException;

/**
 * This is a command that gets a property from the reader and puts in in the
 * supplied hashmap
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AlienPropertyCommand extends AbstractAlien9800Command {

	/**
	 * The shared hashmap containing all the reader properties from the
	 * Alien9800Reader
	 */
	private Map<String, String> attributes;
	/** The command to execute */
	private List<AlienCommandObjectWrapper> commands;
	/** The logger for this class */
	private static final Log logger = LogFactory
			.getLog(AlienPropertyCommand.class);

	/**
	 * Constructor
	 * 
	 * @param commandID
	 *            The ID for this command
	 * @param attributes
	 *            The shared property map from the Alien9800Reader
	 * @param command
	 *            The command to exeucte
	 */
	public AlienPropertyCommand(String commandID,
			Map<String, String> attributes,
			List<AlienCommandObjectWrapper> commands) {
		super(commandID);
		this.attributes = attributes;
		this.commands = commands;
	}

	@Override
	public void run() {
		try {
			for (AlienCommandObjectWrapper wrapper : commands) {
				wrapper.getCommandObject().setSession(
						(Alien9800ReaderSession) this.readerSession);
				try {
					this.attributes.put(wrapper.getPropertyName(), wrapper
							.getCommandObject().execute());
				} catch (AlienException e) {
					logger.warn("Alien Exception while executing command ", e);
				}
			}
		} catch (IOException e) {
			logger.warn("IOException while executing command ", e);
		}

	}
}
