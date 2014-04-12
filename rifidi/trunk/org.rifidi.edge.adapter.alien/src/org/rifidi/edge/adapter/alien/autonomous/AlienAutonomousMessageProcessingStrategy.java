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
/**
 * 
 */
package org.rifidi.edge.adapter.alien.autonomous;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.adapter.alien.messages.AlienMessage;
import org.rifidi.edge.adapter.alien.messages.AlienTag;
import org.rifidi.edge.adapter.alien.messages.AlienTagReadEventFactory;
import org.rifidi.edge.notification.ReadCycle;
import org.rifidi.edge.notification.TagReadEvent;
import org.rifidi.edge.sensors.SensorSession;
import org.rifidi.edge.sensors.sessions.MessageProcessingStrategy;

/**
 * This class is a handler for a single open socket with an Alien reader that is
 * sending data when it is in autonomous mode
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AlienAutonomousMessageProcessingStrategy implements
		MessageProcessingStrategy {

	/** The logger for this class */
	private Log logger = LogFactory
			.getLog(AlienAutonomousMessageProcessingStrategy.class);
	/** Session for this Handler */
	private SensorSession session;
	/** A factory for converting alien tag reads to TagReadEvents */
	private AlienTagReadEventFactory tagReadEventFactory;

	/**
	 * Create a new Handler for a connection with an Alien Reader in autonomous
	 * mode
	 * 
	 * @param stream
	 *            The input stream from the socket
	 * @throws IOException
	 */
	public AlienAutonomousMessageProcessingStrategy(SensorSession session) {
		this.session = session;
		this.tagReadEventFactory = new AlienTagReadEventFactory(session
				.getSensor().getID());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.sensors.base.threads.MessageProcessingStrategy#
	 * processMessage(byte[])
	 */
	@Override
	public void processMessage(byte[] byteMessage) {
		String rawMessage = new String(byteMessage);
		AlienMessage message = new AlienMessage(rawMessage);
		if (logger.isDebugEnabled()) {
			logger.debug("Tag: " + message);
		}
		Set<TagReadEvent> tagReadEvents = new HashSet<TagReadEvent>();
		for (AlienTag tag : message.getTagList()) {
			tagReadEvents.add(tagReadEventFactory.getTagReadEvent(tag));
		}
		ReadCycle readCycle = new ReadCycle(tagReadEvents, session.getSensor()
				.getID(), System.currentTimeMillis());
		session.getSensor().send(readCycle);
		
		//TODO: SEND HERE
		//template.send(new ReadCycleMessageCreator(readCycle));
	}

}
