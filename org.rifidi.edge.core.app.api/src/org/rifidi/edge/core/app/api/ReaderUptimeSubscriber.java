/*
 *  ReaderUptimeSubscriber.java
 *
 *  Created:	Mar 23, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.core.app.api;

import org.rifidi.edge.core.app.api.events.ReaderEvent;

/**
 * @author Matthew Dean
 *
 */
public interface ReaderUptimeSubscriber {
	/**
	 * Whenever a reader event occurs, this method will be called.  
	 * 
	 * @param event
	 */
	public void handleUptimeEvent(ReaderEvent event);
}
