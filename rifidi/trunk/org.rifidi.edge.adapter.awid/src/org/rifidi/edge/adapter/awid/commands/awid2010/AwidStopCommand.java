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
package org.rifidi.edge.adapter.awid.commands.awid2010;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.adapter.awid.awid2010.AwidSession;
import org.rifidi.edge.adapter.awid.awid2010.communication.commands.StopCommand;
import org.rifidi.edge.adapter.awid.awid2010.communication.messages.AckMessage;
import org.rifidi.edge.sensors.ByteMessage;
import org.rifidi.edge.sensors.TimeoutCommand;

/**
 * A command to stop commands running on the Awid
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AwidStopCommand extends TimeoutCommand {

	private static final Log logger = LogFactory.getLog(AwidStopCommand.class);

	public AwidStopCommand(String commandID) {
		super(commandID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.commands.TimeoutCommand#execute()
	 */
	@Override
	protected void execute() throws TimeoutException {
		StopCommand command = new StopCommand();
		AwidSession session = ((AwidSession) super.sensorSession);
		try {
			session.getEndpoint().clearUndeliveredMessages();
			session.sendMessage(command);
			ByteMessage response = session.getEndpoint().receiveMessage();
			AckMessage ack = new AckMessage(response.message);
		} catch (IOException e) {
			logger.warn("PortalID Command did not complete because "
					+ "there was a problem with the session: " + session);
		}

	}

}
