/*
 *  NotifierServiceWrapper.java
 *
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
package org.rifidi.edge.readerplugin.llrp;

import java.util.Dictionary;

import org.rifidi.edge.core.services.notification.NotifierService;

public class NotifierServiceWrapper {
	/** The JMS notifier */
	private NotifierService notifierService;

	/**
	 * Gets the JMS notifier.  
	 * 
	 * @return
	 */
	public NotifierService getNotifierService() {
		return notifierService;
	}

	/**
	 * Called by spring.
	 * 
	 * @param service
	 */
	public void bindNotifierService(NotifierService service,
			Dictionary<String, String> parameters) {
		this.notifierService = service;
	}

	/**
	 * Called by spring.
	 * 
	 * @param service
	 */
	public void unbindNotifierService(NotifierService service,
			Dictionary<String, String> parameters) {
		this.notifierService = null;
	}

}
