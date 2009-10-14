/**
 * 
 */
package com.northwind.rfid.shipping.notifications;

import java.io.Serializable;

/**
 * This class defines an Item Departure Event
 * 
 * @author Kyle Neuemeier - kyle@pramari.com
 * 
 */
public class ItemDepartureNotification implements Serializable {
	/** Default Serial ID */
	private static final long serialVersionUID = 1L;
	/** The zone that this tag departed from */
	private final EZone zone;
	/** The tag that departed */
	private final String tag_Id;

	/**
	 * @param zone
	 *            The zone that this tag departed from
	 * @param tag_Id
	 *            The ID of the tag that departed
	 */
	public ItemDepartureNotification(EZone zone, String tag_Id) {
		this.zone = zone;
		this.tag_Id = tag_Id;
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
