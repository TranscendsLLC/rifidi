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
package org.rifidi.edge.api.service.appmanager;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.rifidi.edge.api.AppState;
import org.rifidi.edge.api.RifidiApp;
import org.rifidi.edge.services.EsperManagementService;

/**
 * The implementation of the AppManager
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AppManagerImpl implements AppManager {

	/** All loaded applications */
	private final ConcurrentHashMap<Integer, RifidiApp> apps;
	/** If the app cannot be started when it is loaded, queue it here */
	private final CopyOnWriteArraySet<Integer> queuedApps;
	/** The esper service */
	private volatile EsperManagementService esperService;
	/** The number of services so far */
	private int serviceCounter = 0;

	/**
	 * Constructor
	 */
	public AppManagerImpl() {
		apps = new ConcurrentHashMap<Integer, RifidiApp>();
		queuedApps = new CopyOnWriteArraySet<Integer>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.service.appmanager.AppManager#getApps()
	 */
	@Override
	public Map<Integer, RifidiApp> getApps() {
		return new HashMap<Integer, RifidiApp>(apps);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.api.service.appmanager.AppManager#loadGroup(
	 * java.lang.String)
	 */
	@Override
	public void loadGroup(String groupName) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.api.service.appmanager.AppManager#startApp(java
	 * .lang.Integer)
	 */
	@Override
	public void startApp(Integer appID) {
		startApp(appID, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.api.service.appmanager.AppManager#stopApp(java
	 * .lang.Integer)
	 */
	@Override
	public void stopApp(Integer appID) {
		RifidiApp app = apps.get(appID);
		if (app == null)
			return;
		if (app.getState() == AppState.STARTED) {
			app.stop();
		}

	}

	/**
	 * A helper method to load an app
	 * 
	 * @param app
	 */
	private void addApp(RifidiApp app) {
		synchronized (apps) {
			if (apps.containsValue(app))
				return;
			int appID = serviceCounter++;
			apps.put(appID, app);
			startApp(appID, true);
		}
	}

	/**
	 * A helper method to start applications
	 * 
	 * @param appID
	 *            The application ID
	 * @param checkAutomaticStart
	 *            If true, check for the the lazyStart flag of the app before
	 *            starting it.
	 */
	private void startApp(int appID, boolean checkAutomaticStart) {
		RifidiApp app = this.getApps().get(appID);
		if (app == null) {
			return;
		}
		Properties properties = new FilePropertyResolver().reolveProperties(app
				.getGroup(), app.getName());
		app.setAppProperties(properties);

		if (app.getState() == AppState.STOPPED) {
			if (esperService == null) {
				queuedApps.add(appID);
				return;
			}
			app.setEsperService(esperService);
			if (checkAutomaticStart && app.lazyStart()) {
				return;
			} else {
				app.initialize();
				app.start();
			}
		}
	}
	
	/**
	 * Called by spring to set the initial list of Rifidi Apps
	 * 
	 * @param apps
	 */
	public void setRifidiApps(Set<RifidiApp> apps) {
		for (RifidiApp app : apps) {
			addApp(app);
		}
	}

	/**
	 * Bind a new AbstractRifidiApp. Called by spring
	 * 
	 * @param app
	 */
	public void bindApp(RifidiApp app, Dictionary<String, String> parameters) {
		addApp(app);
	}

	/**
	 * Unbind an old AbstractRifidiApp. Called by spring.
	 * 
	 * @param app
	 */
	public void unbindApp(RifidiApp app, Dictionary<String, String> parameters) {
		//System.out.println("UNBIND APP: " + app);
		Integer appIDToRemove = null;
		synchronized (apps) {
			for (Integer i : apps.keySet()) {
				if (apps.get(i).equals(app)) {
					appIDToRemove = i;
					break;
				}
			}
			if (appIDToRemove != null) {
				stopApp(appIDToRemove);
				apps.remove(appIDToRemove);
			}
		}

	}

	/**
	 * Set the esper service. Called by spring.
	 * 
	 * @param esper
	 */
	public void setEsperService(EsperManagementService esper) {
		this.esperService = esper;
		for (Integer appID : queuedApps) {
			startApp(appID, true);
		}
		queuedApps.clear();
	}

}
