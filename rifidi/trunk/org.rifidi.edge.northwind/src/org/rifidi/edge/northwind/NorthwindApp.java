/*
 *  NorthwindApp.java
 *
 *  Created:	Aug 9, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.northwind;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.rifidi.edge.core.app.api.AbstractRifidiApp;
import org.rifidi.edge.core.app.api.service.EsperUtil;
import org.rifidi.edge.core.app.api.service.tagmonitor.ReadZone;
import org.rifidi.edge.core.app.api.service.tagmonitor.ReadZoneMonitoringService;
import org.rifidi.edge.core.app.api.service.tagmonitor.ReadZoneSubscriber;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;
import org.rifidi.edge.northwind.events.DockDoorArrivedEvent;
import org.rifidi.edge.northwind.events.DockDoorDepartedEvent;
import org.rifidi.edge.northwind.events.WeighStationArrivedEvent;
import org.rifidi.edge.northwind.events.WeighStationDepartedEvent;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

/**
 * 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class NorthwindApp extends AbstractRifidiApp {

	String dockdoor_reader = "Alien_1";
	String weighstation_reader = "Alien_2";

	private List<ReadZoneSubscriber> subscriberList = new ArrayList<ReadZoneSubscriber>();

	/** The service for monitoring arrival and departure events */
	private ReadZoneMonitoringService readZoneMonitoringService;

	/**
	 * 
	 * 
	 * @param group
	 * @param name
	 */
	public NorthwindApp(String group, String name) {
		super(group, name);
	}

	/**
	 * Called at startup
	 */
	@Override
	public void _start() {
		super._start();

		/* These statements register the events we will use */
		addEventType(DockDoorArrivedEvent.class);
		addEventType(DockDoorDepartedEvent.class);
		addEventType(WeighStationDepartedEvent.class);
		addEventType(WeighStationArrivedEvent.class);

		ReadZone dock_door = new ReadZone("Alien_1");
		ReadZone weigh_station = new ReadZone("Alien_2");

		NorthwindDockDoorReadZoneSubscriber dock_door_subscriber = new NorthwindDockDoorReadZoneSubscriber(
				this, "dock_door");
		NorthwindWeighStationReadZoneSubscriber weigh_station_subscriber = new NorthwindWeighStationReadZoneSubscriber(
				this, "weigh_station");
		this.subscriberList.add(dock_door_subscriber);
		this.subscriberList.add(weigh_station_subscriber);

		this.readZoneMonitoringService.subscribe(dock_door_subscriber,
				dock_door, 2.0f, TimeUnit.SECONDS);
		this.readZoneMonitoringService.subscribe(weigh_station_subscriber,
				weigh_station, 2.0f, TimeUnit.SECONDS);

		String insert_dockdoor_all = EsperUtil.buildInsertStatement(
				"dock_door", dock_door);
		String insert_weighstation_all = EsperUtil.buildInsertStatement(
				"weigh_station", weigh_station);

		addStatement(insert_dockdoor_all);
		addStatement(insert_weighstation_all);

		StatementAwareUpdateListener dockDoorTagOnTooLongListener = new StatementAwareUpdateListener() {
			@Override
			public void update(EventBean[] arg0, EventBean[] arg1,
					EPStatement arg2, EPServiceProvider arg3) {
				if (arg0 != null) {
					TagReadEvent tag = (TagReadEvent) arg0[0]
							.get("dockarrived.tag");
					System.out.println("TAG SEEN TOO LONG ON DOCK DOOR: "
							+ tag.getTag().getID());
				}

			}
		};
		addStatement("select dockarrived.tag from pattern "
				+ "[every dockarrived=DockDoorArrivedEvent-> "
				+ "timer:interval(10 sec) and not DockDoorDepartedEvent"
				+ "(tag.tag.ID=dockarrived.tag.tag.ID)]",
				dockDoorTagOnTooLongListener);
		StatementAwareUpdateListener weighStationTagOnTooLongListener = new StatementAwareUpdateListener() {
			@Override
			public void update(EventBean[] arg0, EventBean[] arg1,
					EPStatement arg2, EPServiceProvider arg3) {
				if (arg0 != null) {
					TagReadEvent tag = (TagReadEvent) arg0[0]
							.get("weigharrived.tag");
					System.out.println("TAG SEEN TOO LONG ON WEIGH STATION: "
							+ tag.getTag().getID());
				}

			}
		};
		addStatement("select weigharrived.tag from pattern "
				+ "[every weigharrived=WeighStationArrivedEvent-> "
				+ "timer:interval(10 sec) and not WeighStationDepartedEvent"
				+ "(tag.tag.ID=weigharrived.tag.tag.ID)]",
				weighStationTagOnTooLongListener);

		StatementAwareUpdateListener dockDoorToWeighStationListener = new StatementAwareUpdateListener() {
			@Override
			public void update(EventBean[] arg0, EventBean[] arg1,
					EPStatement arg2, EPServiceProvider arg3) {
				if (arg0 != null) {
					TagReadEvent tag = (TagReadEvent) arg0[0]
							.get("dock2weigh.tag");
					System.out.println("Tag moved from dock door to weigh "
							+ "station: " + tag.getTag().getFormattedID());
				}
			}
		};
		// Dock door followed by weigh station (what should happen)
		addStatement("select dock2weigh.tag from pattern "
				+ "[every dock2weigh=DockDoorDepartedEvent-> "
				+ "WeighStationArrivedEvent"
				+ "(tag.tag.ID=dock2weigh.tag.tag.ID) "
				+ "where timer:within(10 min)]", dockDoorToWeighStationListener);

		StatementAwareUpdateListener backwardsListener = new StatementAwareUpdateListener() {
			@Override
			public void update(EventBean[] arg0, EventBean[] arg1,
					EPStatement arg2, EPServiceProvider arg3) {
				if (arg0 != null) {
					TagReadEvent tag = (TagReadEvent) arg0[0]
							.get("weigh2dock.tag");
					System.out.println("Tag moved backwards: "
							+ tag.getTag().getFormattedID());
				}
			}
		};

		// Weigh station followed by dock door (going backwards, should not
		// happen)
		addStatement("select weigh2dock.tag from pattern "
				+ "[every weigh2dock=WeighStationDepartedEvent-> "
				+ "DockDoorArrivedEvent"
				+ "(tag.tag.ID=weigh2dock.tag.tag.ID) "
				+ "where timer:within(10 min)]", backwardsListener);

		//
		addStatement("insert into dockdoorpre select * from DockDoorArrivedEvent");

		StatementAwareUpdateListener dockDoorSkipListener = new StatementAwareUpdateListener() {
			@Override
			public void update(EventBean[] arg0, EventBean[] arg1,
					EPStatement arg2, EPServiceProvider arg3) {
				if (arg0 != null) {
					TagReadEvent tag = (TagReadEvent) arg0[0]
							.get("skipdocktag.tag");
					System.out.println("Tag skipped dock: "
							+ tag.getTag().getFormattedID());
				}
			}
		};
		addStatement("select skipdocktag.tag from WeighStationArrivedEvent as skipdocktag "
				+ "where not exists (select * from dockdoorpre.std:unique(tag) "
				+ "as d where skipdocktag.tag = d.tag)", dockDoorSkipListener);

	}

	/**
	 * Called when stopping
	 */
	@Override
	public void _stop() {
		for (ReadZoneSubscriber e : this.subscriberList) {
			this.readZoneMonitoringService.unsubscribe(e);
		}
	}

	/**
	 * Put an event on Esper.
	 * 
	 * @param event
	 */
	public void sendNorthwindEvent(Object event) {
		this.sendEvent(event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.app.api.AbstractRifidiApp#initialize()
	 */
	@Override
	public void initialize() {

	}

	/**
	 * Called by spring
	 * 
	 * @param rzms
	 */
	public void setReadZoneMonitoringService(ReadZoneMonitoringService rzms) {
		this.readZoneMonitoringService = rzms;
	}

}
