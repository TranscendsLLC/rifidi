/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
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
