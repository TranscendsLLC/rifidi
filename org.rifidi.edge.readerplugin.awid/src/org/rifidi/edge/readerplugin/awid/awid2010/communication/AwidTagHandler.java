/*
 * AwidTagHandler.java
 * 
 * Created:     Oct 20th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */package org.rifidi.edge.readerplugin.awid.awid2010.communication;

import java.util.HashSet;
import java.util.Set;

import org.rifidi.edge.core.services.notification.data.ReadCycle;
import org.rifidi.edge.core.services.notification.data.ReadCycleMessageCreator;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;
import org.rifidi.edge.readerplugin.awid.awid2010.Awid2010Sensor;
import org.springframework.jms.core.JmsTemplate;

/**
 * This class processes received tag messages. It sends them over JMS and puts
 * them on esper.
 * 
 * TODO: this class should be extended to collect tags and periodically send
 * them in one read cycle. This is because the awid reader sends a separate
 * message for each tag that is read, and it would be better to send out one
 * ReadCycle with several tags on it than one ReadCycle per tag.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AwidTagHandler {

	/** Helper class that sends out tag messages over JMS */
	private final JmsTemplate jmsTemplate;
	/** The sensor used to put the ReadCycle on Esper */
	private final Awid2010Sensor sensor;

	/**
	 * Constructor
	 * 
	 * @param jmsTemplate
	 *            Helper class that sends out tag messages over JMS
	 * @param sensor
	 *            The sensor used to put the ReadCycle on Esper
	 */
	public AwidTagHandler(JmsTemplate jmsTemplate, Awid2010Sensor sensor) {
		this.jmsTemplate = jmsTemplate;
		this.sensor = sensor;
	}

	/**
	 * Process a tag message
	 * 
	 * @param tagRead
	 *            The Tag that was seen on esper
	 */
	public void handleTagEvent(TagResponseMessage tagRead) {
		Set<TagReadEvent> tagSet = new HashSet<TagReadEvent>();
		tagSet.add(tagRead.getTagReadEvent());
		ReadCycle rc = new ReadCycle(tagSet, sensor.getID(), System
				.currentTimeMillis());
		sensor.send(rc);
		jmsTemplate.send(new ReadCycleMessageCreator(rc));
	}

}
