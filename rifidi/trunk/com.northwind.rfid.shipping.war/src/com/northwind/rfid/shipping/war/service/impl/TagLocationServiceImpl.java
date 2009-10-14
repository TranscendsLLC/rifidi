package com.northwind.rfid.shipping.war.service.impl;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.northwind.rfid.shipping.notifications.AlertNotification;
import com.northwind.rfid.shipping.war.service.TagLocationService;

/**
 * Thread-safe implementation of TagLocationService and
 * TagLocationServiceManager interfaces.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class TagLocationServiceImpl implements TagLocationService,
		TagLocationServiceManager {

	/** All the items that can be seen at dock door */
	private final Set<String> dockDoor = Collections
			.synchronizedSet(new LinkedHashSet<String>());
	/** All the items that can be seen at the weigh station */
	private final Set<String> weighstation = Collections
			.synchronizedSet(new LinkedHashSet<String>());
	/** All the alerts that have been created */
	private final List<AlertNotification> alerts = Collections
			.synchronizedList(new LinkedList<AlertNotification>());

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.northwind.rfid.shipping.war.service.TagLocationService#getAlerts()
	 */
	@Override
	public List<AlertNotification> getAlerts() {
		return new LinkedList<AlertNotification>(alerts);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.northwind.rfid.shipping.war.service.TagLocationService#getDockDoorItems
	 * ()
	 */
	@Override
	public List<String> getDockDoorItems() {
		return new LinkedList<String>(dockDoor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.northwind.rfid.shipping.war.service.TagLocationService#
	 * getWeighStationItems()
	 */
	@Override
	public List<String> getWeighStationItems() {
		return new LinkedList<String>(weighstation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.northwind.rfid.shipping.war.service.impl.TagLocationServiceManager
	 * #Alert(com.northwind.rfid.shipping.notifications.AlertNotification)
	 */
	@Override
	public void Alert(AlertNotification notification) {
		this.alerts.add(notification);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.northwind.rfid.shipping.war.service.impl.TagLocationServiceManager
	 * #PackageArrivedAtDockDoor(java.lang.String)
	 */
	@Override
	public void PackageArrivedAtDockDoor(String item) {
		this.dockDoor.add(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.northwind.rfid.shipping.war.service.impl.TagLocationServiceManager
	 * #PackageArrivedAtWeighStation(java.lang.String)
	 */
	@Override
	public void PackageArrivedAtWeighStation(String item) {
		this.weighstation.add(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.northwind.rfid.shipping.war.service.impl.TagLocationServiceManager
	 * #PackageDepartedFromDockDoor(java.lang.String)
	 */
	@Override
	public void PackageDepartedFromDockDoor(String item) {
		this.dockDoor.remove(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.northwind.rfid.shipping.war.service.impl.TagLocationServiceManager
	 * #PackageDepartedFromWeighStation(java.lang.String)
	 */
	@Override
	public void PackageDepartedFromWeighStation(String item) {
		this.weighstation.remove(item);
	}
}