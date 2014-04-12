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

import java.util.HashSet;
import java.util.Set;

import org.rifidi.edge.api.AbstractRifidiApp;
import org.rifidi.edge.api.resources.JMSResource;
import org.rifidi.edge.api.service.sensormonitor.SensorStatusMonitoringService;
import org.rifidi.edge.api.service.sensormonitor.SensorStatusSubscriber;
import org.rifidi.edge.notification.SensorStatusEvent;

/**
 * @author kyle
 * 
 */
public class SensorMonitor extends AbstractRifidiApp implements
		SensorStatusSubscriber {

	private SensorStatusMonitoringService service;
	private JMSResource jmsTextSender;

	/**
	 * Constructor
	 * 
	 * @param group
	 *            the group this app is a part of
	 * @param name
	 *            The name of the app
	 */
	public SensorMonitor(String group, String name) {
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
		Set<String> sensors = new HashSet<String>();
		this.service.subscribe(this, sensors);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.app.api.AbstractRifidiApp#_stop()
	 */
	@Override
	protected void _stop() {
		super._stop();
		if (service != null) {
			service.unsubscribe(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.app.api.service.sensormonitor.SensorStatusSubscriber
	 * #handleSensorStatusEvent(org.rifidi.edge.core.services.notification.data.
	 * management.SensorStatusEvent)
	 */
	@Override
	public void handleSensorStatusEvent(SensorStatusEvent event) {
		jmsTextSender.sendTextMessage(event.toString());;

	}

	/**
	 * @param service
	 *            the service to set
	 */
	public void setSensorMonitoringService(SensorStatusMonitoringService service) {
		this.service = service;
	}

	/**
	 * Called by spring
	 * 
	 * @param jmsTextSender
	 *            the jmsTextSender to set
	 */
	public void setJmsTextSender(JMSResource jmsTextSender) {
		this.jmsTextSender = jmsTextSender;
	}

}
