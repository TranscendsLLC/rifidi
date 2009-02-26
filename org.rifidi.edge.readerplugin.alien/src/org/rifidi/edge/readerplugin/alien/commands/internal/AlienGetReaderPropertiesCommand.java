/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.commands.internal;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.commands.Command;
import org.rifidi.edge.core.commands.CommandState;
import org.rifidi.edge.readerplugin.alien.Alien9800Reader;
import org.rifidi.edge.readerplugin.alien.Alien9800ReaderConfiguration;
import org.rifidi.edge.readerplugin.alien.commandobject.AlienCommandObject;

/**
 * @author kyle
 * 
 */
public class AlienGetReaderPropertiesCommand extends Command {

	private HashMap<String, String> attributes;
	private static final Log logger = LogFactory
			.getLog(AlienGetReaderPropertiesCommand.class);

	public AlienGetReaderPropertiesCommand(HashMap<String, String> attributes) {
		super();
		this.attributes = attributes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.commands.Command#stop()
	 */
	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public CommandState call() throws Exception {
		try {

			/**
			 * GET READ ONLY PROPERTIES
			 */
			AlienCommandObject aco = new AlienCommandObject(
					Alien9800Reader.COMMAND_READER_TYPE, getReader());
			attributes.put(Alien9800ReaderConfiguration.PROP_READER_TYPE, aco
					.executeGet());
			aco = new AlienCommandObject(
					Alien9800Reader.COMMAND_EXTERNAL_OUTPUT, getReader());
			attributes.put(Alien9800ReaderConfiguration.PROP_EXTERNAL_OUTPUT,
					aco.executeGet());
			aco = new AlienCommandObject(Alien9800Reader.COMMAND_MAC_ADDRESS,
					getReader());
			attributes.put(Alien9800ReaderConfiguration.PROP_MAC_ADDRESS, aco
					.executeGet());
			aco = new AlienCommandObject(Alien9800Reader.COMMAND_MAX_ANTENNA,
					getReader());
			attributes.put(Alien9800ReaderConfiguration.PROP_MAX_ANTENNA, aco
					.executeGet());
			aco = new AlienCommandObject(
					Alien9800Reader.COMMAND_EXTERNAL_INPUT, getReader());
			attributes.put(Alien9800ReaderConfiguration.PROP_EXTERNAL_INPUT,
					aco.executeGet());
			aco = new AlienCommandObject(Alien9800Reader.COMMAND_UPTIME,
					getReader());
			attributes.put(Alien9800ReaderConfiguration.PROP_UPTIME, aco
					.executeGet());
			aco = new AlienCommandObject(Alien9800Reader.COMMAND_READER_VERSION,
					getReader());
			attributes.put(Alien9800ReaderConfiguration.PROP_READER_VERSION, aco
					.executeGet());

			/**
			 * SET PROPERTIES
			 */
			aco = new AlienCommandObject(
					Alien9800Reader.COMMAND_RF_ATTENUATION, getReader());
			String val = attributes
					.get(Alien9800ReaderConfiguration.PROP_RF_ATTENUATION);

			attributes.put(Alien9800ReaderConfiguration.PROP_RF_ATTENUATION,
					aco.executeSet(val));

			aco = new AlienCommandObject(
					Alien9800Reader.COMMAND_EXTERNAL_OUTPUT, getReader());
			val = attributes
					.get(Alien9800ReaderConfiguration.PROP_EXTERNAL_OUTPUT);
			logger.debug("value : " + val);
			attributes.put(Alien9800ReaderConfiguration.PROP_EXTERNAL_OUTPUT,
					aco.executeSet(val));

		} catch (IOException e) {
			return CommandState.LOSTCONNECTION;
		}
		return CommandState.DONE;
	}

}
