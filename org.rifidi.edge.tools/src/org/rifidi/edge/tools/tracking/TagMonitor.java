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
	
	@Override
	public boolean lazyStart() {
		
		String lazyStart= getProperty(LAZY_START, "true");
		return Boolean.parseBoolean(lazyStart);
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
