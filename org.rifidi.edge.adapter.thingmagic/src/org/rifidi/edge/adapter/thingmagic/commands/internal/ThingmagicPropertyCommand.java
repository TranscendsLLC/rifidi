/*
 *  ThingmagicPropertyCommand.java
 *
 *  Created:	Sep 29, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.adapter.thingmagic.commands.internal;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.adapter.thingmagic.AbstractThingmagicCommand;
import org.rifidi.edge.adapter.thingmagic.ThingmagicReaderSession;
import org.rifidi.edge.adapter.thingmagic.commandobject.ThingmagicCommandObjectWrapper;
import org.rifidi.edge.adapter.thingmagic.commandobject.ThingmagicException;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class ThingmagicPropertyCommand extends AbstractThingmagicCommand {

	/**
	 * The shared hashmap containing all the reader properties from the
	 * Alien9800Reader
	 */
	private Map<String, String> attributes;
	/** The command to execute */
	private List<ThingmagicCommandObjectWrapper> commands;
	/** The logger for this class */
	private static final Log logger = LogFactory
			.getLog(ThingmagicPropertyCommand.class);
	
	/**
	 * 
	 * @param commandID
	 */
	public ThingmagicPropertyCommand(String commandID,
			Map<String, String> attributes,
			List<ThingmagicCommandObjectWrapper> commands) {
		super(commandID);
		this.attributes = attributes;
		this.commands = commands;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			for (ThingmagicCommandObjectWrapper wrapper : commands) {
				wrapper.getCommandObject().setSession(
						(ThingmagicReaderSession) this.sensorSession);
				try {
					this.attributes.put(wrapper.getPropertyName(), wrapper
							.getCommandObject().execute());
				} catch (ThingmagicException e) {
					logger.warn("Alien Exception while executing command "
							+ wrapper.getPropertyName(), e);
				}
			}
		} catch (IOException e) {
			logger.warn("IOException while executing command ", e);
		}
	}

}
