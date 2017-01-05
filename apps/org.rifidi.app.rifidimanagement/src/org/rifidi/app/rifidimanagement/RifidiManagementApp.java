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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.AbstractRifidiApp;
import org.rifidi.edge.api.service.tagmonitor.ReadZone;
import org.rifidi.edge.api.service.tagmonitor.StableSetService;
import org.rifidi.edge.daos.ReaderDAO;
import org.rifidi.edge.sensors.AbstractSensor;
import org.rifidi.edge.sensors.SensorSession;

/**
 * A simple application to test available rifidi API commands
 * 
 * @author Manuel Tobon - manuel@transcends.co
 */
public class RifidiManagementApp extends AbstractRifidiApp {

	/**
	 * The stableSet service: returns a list of tags representing a "group"
	 * after an allotted time has passed with no new tags arriving. For
	 * instance, if you place 30 tags in the readzone of the reader, assuming
	 * all tags can be read correctly it will return a list of 30 tags assuming
	 * no new tags are seen for the duration given
	 */
	private StableSetService stableSetService;

	/** Reader DAO -- used to stop reader sessions */
	/**
	 * Injected from spring, if you need to stop reader sessions for a
	 * particular reader
	 **/
	private ReaderDAO readerDAO;

	// A list of all stableSetsubscribers that is kept for when the time comes
	// to
	// unsubscribe them.
	private List<MyStableSetSubscriber> stableSetSubscriberList;

	/**
	 * stableSetTime - The time that must pass with no new tags having been seen
	 * before the stable set will return. It's used for stableSet Service
	 */
	private Float stableSetTime;

	/** Logger for this class */
	private final Log logger = LogFactory.getLog(getClass());

	public RifidiManagementApp(String group, String name) {
		super(group, name);
	}

	/**
	 * @return the stableSetService
	 */
	public StableSetService getStableSetService() {
		return stableSetService;
	}

	/**
	 * @param stableSetService
	 *            the stableSetService to set
	 */
	public void setStableSetService(StableSetService stableSetService) {
		this.stableSetService = stableSetService;
	}

	/**
	 * Inject the reader DAO to allow this app to stop the reader.
	 * 
	 * @param readerDAO
	 */
	// Used by spring framework to inject the reader dao
	public void setReaderDAO(ReaderDAO readerDAO) {
		this.readerDAO = readerDAO;
	}

	/**
	 * @return the readerDAO
	 */
	private ReaderDAO getReaderDAO() {
		return readerDAO;
	}

	/**
	 * @return the stableSetTime
	 */

	public Float getStableSetTime() {
		return stableSetTime;
	}

	/**
	 * @param stableSetTime
	 *            the stableSetTime to set
	 */

	public void setStableSetTime(Float stableSetTime) {
		this.stableSetTime = stableSetTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.AbstractRifidiApp#_start()
	 */
	@Override
	public void _start() {
		super._start();

		stableSetSubscriberList = new LinkedList<MyStableSetSubscriber>();

		// Get all the read zones
		HashMap<String, ReadZone> allReadZones = super.getReadZones();

		// Get the front door read zone. According to this readzone properties
		// file,
		// Front_Door readzone contains only one reader
		ReadZone frontDoorReadZone = allReadZones.get("Front_Door");

		// Get the back door read zone. According to this readzone properties
		// file,
		// Back_Door readzone contains one or many readers
		ReadZone backDoorReadZone = allReadZones.get("Back_Door");

		// Subscribe the frontDoorReadZone to stableSetService
		subscribeToStableSetService(frontDoorReadZone);

		// Subscribe the backDoorReadZone to stableSetService
		subscribeToStableSetService(backDoorReadZone);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.AbstractRifidiApp#_stop()
	 */
	@Override
	public void _stop() {

		// Unsubscribe from stableset service
		for (MyStableSetSubscriber sub : this.stableSetSubscriberList) {
			unsubscribeFromStableSetService(sub);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.AbstractRifidiApp#initialize()
	 */
	@Override
	public void initialize() {

		// Set stableset time from properties
		setStableSetTime(Float.parseFloat(getProperty("stableSetTime", null)));

	}

	/**
	 * Stop any currently running sessions for the given reader ID.
	 * 
	 * @param readerID
	 */
	public void stopReaderSession(String readerID) {
		AbstractSensor<?> sensor = getReaderDAO().getReaderByID(readerID);
		if (sensor != null) {
			Map<String, SensorSession> sessions = sensor.getSensorSessions();
			for (String s : sessions.keySet()) {
				sessions.get(s).disconnect();
			}

			logger.debug("Disconnected sessions for reader id " + readerID);

		} else {

			logger.error("Error disconnecting sessions for reader id "
					+ readerID + ". No reader with this name found.");

		}
	}

	
	/**
	 * 
	 * @param readZone
	 *            the readZone to subscribe
	 */
	public void subscribeToStableSetService(ReadZone readZone) {

		// Create an instance of the stableset subscriber
		MyStableSetSubscriber myStableSetSubscriber = new MyStableSetSubscriber(
				this, readZone);

		stableSetSubscriberList.add(myStableSetSubscriber);

		// Create a list of readzones
		List<ReadZone> readZoneList = new LinkedList<ReadZone>();

		// Add the readzone to the readzone list and subscribe
		readZoneList.add(readZone);

		getStableSetService().subscribe(myStableSetSubscriber, readZoneList,
				getStableSetTime(), TimeUnit.SECONDS, true, true);

		logger.info("subscribed readzone with reader id: "
				+ readZone.getReaderID() + " and readerStableSetTime: "
				+ getStableSetTime());

	}

	/**
	 * 
	 * @param stableSetSubscriber
	 */
	public void unsubscribeFromStableSetService(
			MyStableSetSubscriber myStableSetSubscriber) {

		getStableSetService().unsubscribe(myStableSetSubscriber);

		logger.info("unsubscribed subscriber with reader id: "
				+ myStableSetSubscriber.getReadZone().getReaderID());

	}
}
