/**
 * 
 */
package com.northwind.rfid.shipping.war.service.impl;

import com.northwind.rfid.shipping.notifications.AlertNotification;

/**
 * The manager of the TagLocationService. It allows a component to add data.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface TagLocationServiceManager {

	/**
	 * Called when a new package has arrived at the dockdoor
	 * 
	 * @param item
	 */
	void PackageArrivedAtDockDoor(String item);

	/**
	 * Called when a package departs from the dock door
	 * 
	 * @param item
	 */
	void PackageDepartedFromDockDoor(String item);

	/**
	 * Called when an item arrives at the weigh station
	 * 
	 * @param item
	 */
	void PackageArrivedAtWeighStation(String item);

	/**
	 * Called when an item departs from the weigh station
	 * 
	 * @param item
	 */
	void PackageDepartedFromWeighStation(String item);

	/**
	 * Called when a new alert happens
	 * 
	 * @param notification
	 */
	void Alert(AlertNotification notification);

}
