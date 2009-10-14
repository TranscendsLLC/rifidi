/**
 * 
 */
package com.northwind.rfid.shipping.notifications;

import java.io.Serializable;

/**
 * This class defines an Alert Event.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AlertNotification implements Serializable {

	/** Default Serial ID */
	private static final long serialVersionUID = 1L;
	/** The alert message */
	private final EAlertType alert;
	/** The tag that the alert applies to */
	private final String tagID;

	/**
	 * @param alert
	 * @param tagID
	 */
	public AlertNotification(EAlertType alert, String tagID) {
		super();
		this.alert = alert;
		this.tagID = tagID;
	}

	/**
	 * @return the alert
	 */
	public EAlertType getAlert() {
		return alert;
	}

	/**
	 * @return the tagID
	 */
	public String getTag_Id() {
		return tagID;
	}

	/**
	 * @return A message that describes this alert type
	 */
	public String getMessage() {
		switch (alert) {
		case Package_is_Lost:
			return "Package is lost";
		case Package_Moved_Backwards:
			return "Package moved backwards";
		case Package_Skipped_Dock_Door:
			return "Package skipped dock door";
		default:
			return "Unknown alert";
		}
	}
}
