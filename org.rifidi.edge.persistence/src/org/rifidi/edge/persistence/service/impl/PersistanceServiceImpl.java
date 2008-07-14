/* 
 * PersistanceServiceImpl.java
 *  Created:	Jun 20, 2006
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.persistence.service.impl;

import org.rifidi.edge.core.exceptions.RifidiReaderInfoNotFoundException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.service.ReaderPluginListener;
import org.rifidi.edge.core.readerplugin.service.ReaderPluginService;
import org.rifidi.edge.core.readersession.ReaderSession;
import org.rifidi.edge.core.readersession.service.ReaderSessionListener;
import org.rifidi.edge.core.readersession.service.ReaderSessionService;
import org.rifidi.edge.persistence.service.PersistenceService;
import org.rifidi.edge.persistence.xml.PersistedReaderInfo;
import org.rifidi.services.annotations.Inject;

/**
 * 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class PersistanceServiceImpl implements PersistenceService,
		ReaderPluginListener, ReaderSessionListener {
	
	/**
	 * 
	 */
	private PersistedReaderInfo pri;
	
	/**
	 * 
	 */
	private ReaderSessionService readerSessionService;
	
	/**
	 * 
	 */
	private ReaderPluginService readerPluginService;

	/**
	 * 
	 */
	public PersistanceServiceImpl() {
		pri = new PersistedReaderInfo();
		System.out.println("created");
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.service.ReaderPluginListener#readerPluginRegisteredEvent(java.lang.Class)
	 */
	@Override
	public void readerPluginRegisteredEvent(
			Class<? extends ReaderInfo> readerInfo) {
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.service.ReaderPluginListener#readerPluginUnregisteredEvent(java.lang.Class)
	 */
	@Override
	public void readerPluginUnregisteredEvent(
			Class<? extends ReaderInfo> readerInfo) {
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.core.readersession.service.ReaderSessionListener#addEvent(org.rifidi.edge.core.readersession.ReaderSession)
	 */
	@Override
	public void addEvent(ReaderSession readerSession) {
		pri.addReaderInfo(readerSession.getReaderInfo());
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.core.readersession.service.ReaderSessionListener#removeEvent(org.rifidi.edge.core.readersession.ReaderSession)
	 */
	@Override
	public void removeEvent(ReaderSession readerSession) {
		 try {
			pri.removeReader(readerSession.getReaderInfo());
		} catch (RifidiReaderInfoNotFoundException e) {
			// TODO: Should we do anything else here?
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.persistence.service.PersistenceService#start(java.lang.String)
	 */
	@Override
	public void start(String fileName) {
		
	}

	/**
	 * 
	 * 
	 * @param readerSessionService
	 */
	@Inject
	public void setReaderSessionService(ReaderSessionService readerSessionService){
		this.readerSessionService = readerSessionService;
	}

}
