/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
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
