/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.commands.internal;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.readerplugin.alien.AbstractAlien9800Command;
import org.rifidi.edge.readerplugin.alien.Alien9800Reader;
import org.rifidi.edge.readerplugin.alien.Alien9800ReaderSession;
import org.rifidi.edge.readerplugin.alien.commandobject.AlienCommandObject;
import org.rifidi.edge.readerplugin.alien.commandobject.AlienException;

/**
 * @author kyle
 * 
 */
public class AlienGetReaderPropertiesCommand extends AbstractAlien9800Command {

	private HashMap<String, String> attributes;
	private static final Log logger = LogFactory
			.getLog(AlienGetReaderPropertiesCommand.class);

	public AlienGetReaderPropertiesCommand(String commandID, HashMap<String, String> attributes) {
		super(commandID);
		this.attributes = attributes;
	}

	@Override
	public void run() {
		try {

			/**
			 * GET READ ONLY PROPERTIES
			 */
			AlienCommandObject aco = new AlienCommandObject(
					Alien9800ReaderSession.COMMAND_READER_TYPE, (Alien9800ReaderSession) readerSession);
			attributes.put(Alien9800Reader.PROP_READER_TYPE, aco
					.executeGet());
			aco = new AlienCommandObject(
					Alien9800ReaderSession.COMMAND_EXTERNAL_OUTPUT, (Alien9800ReaderSession) readerSession);
			attributes.put(Alien9800Reader.PROP_EXTERNAL_OUTPUT,
					aco.executeGet());
			aco = new AlienCommandObject(Alien9800ReaderSession.COMMAND_MAC_ADDRESS,
					(Alien9800ReaderSession) readerSession);
			attributes.put(Alien9800Reader.PROP_MAC_ADDRESS, aco
					.executeGet());
			aco = new AlienCommandObject(Alien9800ReaderSession.COMMAND_MAX_ANTENNA,
					(Alien9800ReaderSession) readerSession);
			attributes.put(Alien9800Reader.PROP_MAX_ANTENNA, aco
					.executeGet());
			aco = new AlienCommandObject(
					Alien9800ReaderSession.COMMAND_EXTERNAL_INPUT, (Alien9800ReaderSession) readerSession);
			attributes.put(Alien9800Reader.PROP_EXTERNAL_INPUT,
					aco.executeGet());
			aco = new AlienCommandObject(Alien9800ReaderSession.COMMAND_UPTIME,
					(Alien9800ReaderSession) readerSession);
			attributes.put(Alien9800Reader.PROP_UPTIME, aco
					.executeGet());
			aco = new AlienCommandObject(Alien9800ReaderSession.COMMAND_READER_VERSION,
					(Alien9800ReaderSession) readerSession);
			attributes.put(Alien9800Reader.PROP_READER_VERSION, aco
					.executeGet());

			/**
			 * SET PROPERTIES
			 */
			aco = new AlienCommandObject(
					Alien9800ReaderSession.COMMAND_RF_ATTENUATION, (Alien9800ReaderSession) readerSession);
			String val = attributes
					.get(Alien9800Reader.PROP_RF_ATTENUATION);

			attributes.put(Alien9800Reader.PROP_RF_ATTENUATION,
					aco.executeSet(val));
			
			aco = new AlienCommandObject(
					Alien9800ReaderSession.COMMAND_EXTERNAL_OUTPUT, (Alien9800ReaderSession) readerSession);
			val = attributes
					.get(Alien9800Reader.PROP_EXTERNAL_OUTPUT);
			
			attributes.put(Alien9800Reader.PROP_EXTERNAL_OUTPUT,
					aco.executeSet(val));

		} catch (IOException e) {
			logger.warn("There was an IOException", e);
		} catch (AlienException e) {
			logger.warn("There was an AlienException", e);
		}
		
	}

}
