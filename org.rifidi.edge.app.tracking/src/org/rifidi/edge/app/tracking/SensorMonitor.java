/**
 * 
 */
package org.rifidi.edge.app.tracking;

import java.util.HashSet;
import java.util.Set;

import org.rifidi.edge.core.app.api.JMSRifidiApp;
import org.rifidi.edge.core.app.api.service.sensormonitor.SensorStatusMonitoringService;
import org.rifidi.edge.core.app.api.service.sensormonitor.SensorStatusSubscriber;
import org.rifidi.edge.core.services.notification.data.management.SensorStatusEvent;

/**
 * @author kyle
 * 
 */
public class SensorMonitor extends JMSRifidiApp implements
		SensorStatusSubscriber {

	private SensorStatusMonitoringService service;

	/**
	 * Constructor
	 * 
	 * @param group
	 *            the group this app is a part of
	 * @param name
	 *            The name of the app
	 */
	public SensorMonitor(String group, String name) {
		super(group,name);
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
		super.sendTextMessage(event.toString());

	}

	/**
	 * @param service
	 *            the service to set
	 */
	public void setSensorMonitoringService(SensorStatusMonitoringService service) {
		this.service = service;
	}
}
