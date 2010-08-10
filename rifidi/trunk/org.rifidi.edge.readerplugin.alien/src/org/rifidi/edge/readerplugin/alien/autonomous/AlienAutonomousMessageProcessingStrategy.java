/*
 * 
 * AlienAutonomousMessageProcessingStrategy.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.autonomous;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.sensors.SensorSession;
import org.rifidi.edge.core.sensors.sessions.MessageProcessingStrategy;
import org.rifidi.edge.core.services.notification.data.ReadCycle;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;
import org.rifidi.edge.readerplugin.alien.messages.AlienMessage;
import org.rifidi.edge.readerplugin.alien.messages.AlienTag;
import org.rifidi.edge.readerplugin.alien.messages.AlienTagReadEventFactory;

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
