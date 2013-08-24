/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.tools.tracking;

import org.rifidi.edge.api.AbstractRifidiApp;
import org.rifidi.edge.api.resources.JMSResource;
import org.rifidi.edge.notification.TagReadEvent;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

/**
 * Sends tags out over jms with this format:
 * 
 * tag:12345|time:1234556899|readerid:alien_1|antennaid:2|rssi:2.4
 * 
 * @author kyle
 * 
 */
public class TagMonitor extends AbstractRifidiApp {

	private JMSResource jmsTextSender;
	
	/**
	 * 
	 * @param group
	 * @param name
	 */
	public TagMonitor(String group, String name) {
		super(group, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.app.api.AbstractRifidiApp#_start()
	 */
	@Override
	protected void _start() {
		super._start();
		StatementAwareUpdateListener listener = new StatementAwareUpdateListener() {

			@Override
			public void update(EventBean[] arg0, EventBean[] arg1,
					EPStatement arg2, EPServiceProvider arg3) {
				if (arg0 != null) {
					for (EventBean bean : arg0) {
						TagReadEvent event = (TagReadEvent) bean
								.getUnderlying();
						jmsTextSender.sendTextMessage(formTagMessage(event));
					}
				}

			}
		};

		addStatement("select * from ReadCycle[select * from tags]", listener);

	}

	private String formTagMessage(TagReadEvent event) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("tag:" + event.getTag().getFormattedID());
		buffer.append("|time:" + event.getTimestamp());
		buffer.append("|readerid:" + event.getReaderID());
		buffer.append("|antennaid:" + event.getAntennaID());
		if (event.getExtraInformation().containsKey(TagReadEvent.RSSI))
			buffer.append("|rssi:"
					+ event.getExtraInformation().get(TagReadEvent.RSSI));

		return buffer.toString();
	}

	/**
	 * @param jmsTextSender the jmsTextSender to set
	 */
	public void setJmsTextSender(JMSResource jmsTextSender) {
		this.jmsTextSender = jmsTextSender;
	}
	
}
