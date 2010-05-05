/**
 * 
 */
package org.rifidi.edge.app.tracking;

import java.util.Collections;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.app.api.RifidiApp#start()
	 */
	@Override
	public void start() {
		// TODO Auto-generated method stub

	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.app.api.RifidiApp#stop()
	 */
	@Override
	public void stop() {
		super.stop();
		if(service!=null){
			service.unsubscribe(this);
		}
	}



	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.app.api.service.sensormonitor.SensorStatusSubscriber#handleSensorStatusEvent(org.rifidi.edge.core.services.notification.data.management.SensorStatusEvent)
	 */
	@Override
	public void handleSensorStatusEvent(SensorStatusEvent event) {
		super.sendTextMessage(event.toString());
		
	}

	/**
	 * @param service the service to set
	 */
	public void setSensorMonitoringService(SensorStatusMonitoringService service) {
		this.service = service;
		Set<String> sensors = new HashSet<String>();
		sensors.add("Alien_1");
		sensors.add("LLRP_1");
		this.service.subscribe(this, sensors);
		
	}
}
