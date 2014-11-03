/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
package org.rifidi.app.rifidimanagement;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.service.tagmonitor.ReadZone;
import org.rifidi.edge.api.service.tagmonitor.StableSetSubscriber;
import org.rifidi.edge.notification.TagReadEvent;

/**
 * The subscriber class. This class monitors the stableset station tags arrived
 * events for reader.
 * 
 * @author Manuel Tobon - manuel@transcends.co
 */
public class MyStableSetSubscriber implements StableSetSubscriber {

	/**
	 * Reference to application of this subscriber to notify for example when to
	 * unsubscribe this subscriber and stop reader session when stableset has
	 * reached
	 **/
	private RifidiManagementApp rifidiManagementApp;

	/** The read zone for this subscriber **/
	private ReadZone readZone;

	/** Logger for this class */
	private final Log logger = LogFactory.getLog(getClass());

	/**
	 * You can use this kind of constructor where you pass the necessary
	 * parameters
	 **/

	/**
	 * Constructor
	 * 
	 * @param rifidiManagementApp
	 *            the application to associate to this subscriber
	 * @param readZone
	 *            the readzone instance associated with this subscriber
	 */
	public MyStableSetSubscriber(RifidiManagementApp rifidiManagementApp,
			ReadZone readZone) {

		this.rifidiManagementApp = rifidiManagementApp;
		this.readZone = readZone;

	}

	@Override
	public void stableSetReached(Set<TagReadEvent> tagReadEventSet) {

		logger.info("TAG SET ARRIVED: size: " + tagReadEventSet.size());

		// You can unsubscribe this subscriber using the app reference
		rifidiManagementApp.unsubscribeFromStableSetService(this);

		logger.debug("unsubscribed the subscriber for reader id: "
				+ getReadZone().getReaderID());

		// Maybe these tags are read by several readers in this read event (in
		// case that
		// the readzone maps to multiple readers), so you can keep track of all
		// readers
		// in a set as long you iterate over tags
		// you can use a Set, that is a Collection that contains no duplicates
		Set<String> readerIdSet = new HashSet<String>();

		// Iterate over the tags
		for (TagReadEvent tag : tagReadEventSet) {

			// Extract the reader id to keep in a set
			readerIdSet.add(tag.getReaderID());

			logger.debug("tag added to tag list of reader id "
					+ tag.getReaderID() + ": " + tag.getTag().getFormattedID());

		}

		// You can stop the session for every reader
		for (String readerID : readerIdSet) {
			this.rifidiManagementApp.stopReaderSession(readerID);
			logger.debug("info session for reader id: " + readerID);
		}

		try {

			Thread.sleep(500);

		} catch (InterruptedException e) {
			// Don't care
		}

		// Subscribe
		rifidiManagementApp.subscribeToStableSetService(readZone);
		logger.debug("subscribed the subscriber for reader id: "
				+ getReadZone().getReaderID());

	}

	/**
	 * @return the readZone
	 */
	public ReadZone getReadZone() {
		return readZone;
	}

	/**
	 * @param readZone
	 *            the readZone to set
	 */
	public void setReadZone(ReadZone readZone) {
		this.readZone = readZone;
	}

}
