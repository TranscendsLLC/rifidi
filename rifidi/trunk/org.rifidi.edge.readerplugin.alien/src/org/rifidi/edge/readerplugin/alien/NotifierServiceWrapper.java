/*
 *  NotifierServiceWrapper.java
 *
 *  Created:	Mar 9, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.alien;
//TODO: Comments
import java.util.Dictionary;

import org.rifidi.edge.core.notifications.NotifierService;

/**
 * 
 * 
 * @author kyle
 */
public class NotifierServiceWrapper {

	/** The JMS notifier */
	private NotifierService notifierService;

	/**
	 * 
	 * 
	 * @return
	 */
	public NotifierService getNotifierService() {
		return notifierService;
	}

	/**
	 * Called by spring
	 * 
	 * @param service
	 */
	public void bindNotifierService(NotifierService service,
			Dictionary<String, String> parameters) {
		this.notifierService = service;
	}

	/**
	 * Called by spring
	 * 
	 * @param service
	 */
	public void unbindNotifierService(NotifierService service,
			Dictionary<String, String> parameters) {
		this.notifierService = null;
	}

}
