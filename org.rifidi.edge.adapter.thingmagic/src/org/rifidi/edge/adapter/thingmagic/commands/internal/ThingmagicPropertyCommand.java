/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
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
