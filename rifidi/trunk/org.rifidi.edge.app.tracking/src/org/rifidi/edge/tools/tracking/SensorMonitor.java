/**
 * 
 */
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
