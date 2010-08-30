/**
 * 
 */
package org.rifidi.app.example;

import java.util.concurrent.TimeUnit;

import org.rifidi.edge.core.app.api.AbstractRifidiApp;
import org.rifidi.edge.core.app.api.service.EsperUtil;
import org.rifidi.edge.core.app.api.service.tagmonitor.ReadZone;
import org.rifidi.edge.core.app.api.service.tagmonitor.ReadZoneMonitoringService;
import org.rifidi.edge.core.app.api.service.tagmonitor.ReadZoneSubscriber;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

/**
 * This app monitors a read zone and prints out a line when it sees a new tag.
 * 
 * You can test this application without hooking up a real RFID reader by using
 * the TagGenerator diagnostic app. A sample dataset and exposure files have
 * been provided. Type 'startTagRunner group1 exposure1' in the OSGi command
 * prompt to kick it off. The TagGenerator app allows you to send tags right
 * into esper without using the Sensor Layer. It's useful for testing
 * applications. You can read more about the TagGenerator in the developer
 * documentation.
 * 
 */
public class ExampleApp extends AbstractRifidiApp implements ReadZoneSubscriber {

	/** An esper service to monitor the read zone */
	private ReadZoneMonitoringService monitoringService;
	/** A property */
	private String property1;
	/** Another property */
	private String property2;
	/**
	 * The amount of time of not seeing a tag before it is reported as
	 * 'departed'
	 */
	private String timeout;

	public ExampleApp() {
		// 'Templates' is the group name. 'ExampleApp' is the app name
		super("Examples", "ExampleApp");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.app.api.AbstractRifidiApp#_start()
	 */
	@Override
	protected void _start() {
		super._start();

		// properties are read in from the properties file
		System.out.println(property1 + " " + property2);

		// readzones are read in from the readzone directory
		ReadZone readZone = getReadZones().get("dockdoor");

		// Convert the timeout string to a float and a TimeUnit
		float time = EsperUtil.esperTimetoTime(timeout);
		TimeUnit timeUnit = EsperUtil.esperTimetoTimeUnit(timeout);

		// subscribe to the monitoring service to be notified of arrival and
		// departure events.
		monitoringService.subscribe(this, readZone, time, timeUnit);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.app.api.AbstractRifidiApp#_stop()
	 */
	@Override
	protected void _stop() {
		super._stop();

		// if you subscribe, make sure you unsubscribe
		monitoringService.unsubscribe(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.app.api.AbstractRifidiApp#initialize()
	 */
	@Override
	public void initialize() {

		// properties are read in from the properties file in the Template
		// folder
		this.property1 = getProperty("firstProperty", null);
		this.property2 = getProperty("secondProperty", null);

		// you can provide a default value for the property if it's not in the
		// property file
		this.timeout = getProperty("timeout", "5 sec");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.app.api.service.tagmonitor.ReadZoneSubscriber#tagArrived
	 * (org.rifidi.edge.core.services.notification.data.TagReadEvent)
	 */
	@Override
	public void tagArrived(TagReadEvent tag) {
		System.out.println("tag arrived: " + tag);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.app.api.service.tagmonitor.ReadZoneSubscriber#
	 * tagDeparted(org.rifidi.edge.core.services.notification.data.TagReadEvent)
	 */
	@Override
	public void tagDeparted(TagReadEvent tag) {
		System.out.println("tag departed: " + tag);

	}

	/**
	 * This methid is called from spring
	 * 
	 * @param monitoringService
	 *            the monitoringService to set
	 */
	public void setMonitoringService(ReadZoneMonitoringService monitoringService) {
		this.monitoringService = monitoringService;
	}

}
