/**
 * 
 */
package org.rifidi.edge.app.tracking;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.app.api.JMSRifidiApp;
import org.rifidi.edge.core.app.api.service.monitoring.ReadZoneMonitoringService;
import org.rifidi.edge.core.app.api.service.monitoring.ReadZoneSubscriber;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

/**
 * This class keeps track of arrival and departure events.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class TrackingApp extends JMSRifidiApp implements ReadZoneSubscriber {

	/** The Data Access Object */
	private volatile TrackingMessageFactory messageFactory;
	/** The logger for this class */
	private static Log logger = LogFactory.getLog(TrackingApp.class);
	/** The service that monitors read zones. */
	private volatile ReadZoneMonitoringService monitoringService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.app.api.RifidiApp#start()
	 */
	@Override
	public void start() {
		monitoringService.subscribe(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.app.api.RifidiApp#stop()
	 */
	@Override
	public void stop() {
		super.stop();
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
		sendTextMessage(messageFactory.getArrviedMessage(event));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.app.api.service.monitoring.ReadZoneSubscriber#
	 * tagDeparted(org.rifidi.edge.core.services.notification.data.TagReadEvent)
	 */
	@Override
	public void tagDeparted(TagReadEvent event) {
		sendTextMessage(messageFactory.getDepartedMessage(event));

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

}
