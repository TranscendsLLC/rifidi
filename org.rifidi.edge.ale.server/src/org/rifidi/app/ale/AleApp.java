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
package org.rifidi.app.ale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.rifidi.edge.api.AbstractRifidiApp;
import org.rifidi.edge.api.service.tagmonitor.ReadZone;
import org.rifidi.edge.api.service.tagmonitor.ReadZoneMonitoringService;
import org.rifidi.edge.api.service.tagmonitor.ReadZoneSubscriber;
import org.rifidi.edge.server.ale.rest.AleRestoreException;
import org.rifidi.edge.server.epcglobal.alelr.services.RestoreService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A simple application to associate readzones from Rifidi to ALE
 * 
 * @author Manuel Tobon - manuel@transcends.co
 */
public class AleApp 
		extends AbstractRifidiApp {

	/** The service for monitoring arrival and departure events */
	private ReadZoneMonitoringService readZoneMonitoringService;
	private List<ReadZoneSubscriber> subscriberList;
	@Autowired
	private RestoreService restoreService;

	public AleApp(String group, String name) {
		super(group, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.AbstractRifidiApp#_start()
	 */
	@Override
	public void _start() {
		super._start();
		
		// Restore ALE State
	    List<Exception> exceptionList = restoreALEState();
	    
//	    if ( !exceptionList.isEmpty() ){
//	    	throw new AleRestoreException(exceptionList);
//	    }

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.AbstractRifidiApp#_stop()
	 */
	@Override
	public void _stop() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.AbstractRifidiApp#initialize()
	 */
	@Override
	public void initialize() {
	}

	
	public HashMap<String, ReadZone> getReadZones(){
		return super.getReadZones();
	}
	
	private List<Exception> restoreALEState(){
		
		 List<Exception> exceptionList = new ArrayList<>();
		 exceptionList.addAll( restoreService.restoreLRSpecs() );
		 exceptionList.addAll( restoreService.restoreECSpecs() );
		 exceptionList.addAll( restoreService.restoreECSubscribers() );
		 
		 return exceptionList;
	 }
}
