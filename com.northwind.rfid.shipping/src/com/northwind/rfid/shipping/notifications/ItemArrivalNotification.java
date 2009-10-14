/**
 * 
 */
package com.northwind.rfid.shipping.notifications;

import java.io.Serializable;

/**
 * This class defines an Item Arrival Event
 * @author Kyle Neumeier - kyle@pramari.com
 *
 */
public class ItemArrivalNotification implements Serializable{
	/** Default Serial ID */
	private static final long serialVersionUID = 1L;
	/** The zone that this tag arrived at*/
	private final EZone zone;
	/** The tag that arrived */
	private final String tag_Id;

	/**
	 * @param zone
	 *            The zone that this tag arrived at
	 * @param tag_id
	 *            The ID of the tag that arrived
	 */
	public ItemArrivalNotification(EZone zone, String tag_id) {
		this.zone = zone;
		this.tag_Id = tag_id;
	}

	/**
	 * @return the zone
	 */
	public EZone getZone() {
		return zone;
	}

	/**
	 * @return the tag_id
	 */
	public String getTag_Id() {
		return tag_Id;
	}
}
