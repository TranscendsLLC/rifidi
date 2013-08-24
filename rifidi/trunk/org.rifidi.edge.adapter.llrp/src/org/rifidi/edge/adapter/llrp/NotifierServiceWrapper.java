/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.llrp;

import java.util.Dictionary;

import org.rifidi.edge.notification.NotifierService;

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
