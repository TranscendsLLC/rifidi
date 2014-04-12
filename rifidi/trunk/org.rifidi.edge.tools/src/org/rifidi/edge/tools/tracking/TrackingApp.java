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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.AbstractRifidiApp;
import org.rifidi.edge.api.resources.JMSResource;
import org.rifidi.edge.api.service.tagmonitor.ReadZoneMonitoringService;
import org.rifidi.edge.api.service.tagmonitor.ReadZoneSubscriber;
import org.rifidi.edge.notification.TagReadEvent;

/**
 * This class keeps track of arrival and departure events.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class TrackingApp extends AbstractRifidiApp implements ReadZoneSubscriber {

	/** The Data Access Object */
	private volatile TrackingMessageFactory messageFactory;
	/** The logger for this class */
	private static Log logger = LogFactory.getLog(TrackingApp.class);
	/** The service that monitors read zones. */
	private volatile ReadZoneMonitoringService monitoringService;
	private volatile JMSResource jmsTextSender;

	/**
	 * 
	 * @param group
	 * @param name
	 */
	public TrackingApp(String group, String name) {
		super(group, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.app.api.AbstractRifidiApp#_start()
	 */
	@Override
	protected void _start() {
		// super._start();
		// monitoringService.subscribe(this, Collections.EMPTY_LIST, 10f,
		// TimeUnit.SECONDS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.app.api.AbstractRifidiApp#_stop()
	 */
	@Override
	protected void _stop() {
		super._stop();
		monitoringService.unsubscribe(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.app.api.service.monitoring.ReadZoneSubscriber#tagArrived
	 * (org.rifidi.edge.core.services.notification.data.TagReadEvent)
	 */
	@Override
	public void tagArrived(TagReadEvent event) {
		jmsTextSender.sendTextMessage(messageFactory.getArrviedMessage(event));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.app.api.service.monitoring.ReadZoneSubscriber#
	 * tagDeparted(org.rifidi.edge.core.services.notification.data.TagReadEvent)
	 */
	@Override
	public void tagDeparted(TagReadEvent event) {
		jmsTextSender.sendTextMessage(messageFactory.getDepartedMessage(event));

	}

	/**
	 * Called by spring
	 * 
	 * @param service
	 *            The ReadZoneMonitoringService
	 */
	public void setReadZoneMonitoringService(ReadZoneMonitoringService service) {
		this.monitoringService = service;
	}

	/**
	 * Called by spring
	 * 
	 * @param messageFactory
	 */
	public void setTrackingMessageFactory(TrackingMessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}

	/**
	 * @param jmsTextSender the jmsTextSender to set
	 */
	public void setJmsTextSender(JMSResource jmsTextSender) {
		this.jmsTextSender = jmsTextSender;
	}
	
}
