/**
 * 
 */
package com.northwind.rfid.shipping.war.service;

import java.util.List;

import com.northwind.rfid.shipping.notifications.AlertNotification;

/**
 * This service provides the current list of tags at the Dock Door and the Weigh
 * Station, as well as all the alerts that have happened so far
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface TagLocationService {
	/**
	 * Get a list of all packages at the dock door
	 * 
	 * @return
	 */
	List<String> getDockDoorItems();

	/**
	 * Get a list of all packages at the Weigh Station
	 * 
	 * @return
	 */
	List<String> getWeighStationItems();

	/**
	 * Get a list of all the alerts
	 * 
	 * @return
	 */
	List<AlertNotification> getAlerts();
}
