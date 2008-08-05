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

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.exceptions.RifidiReaderInfoNotFoundException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.service.ReaderPluginListener;
import org.rifidi.edge.core.readerplugin.service.ReaderPluginService;
import org.rifidi.edge.core.readersession.ReaderSession;
import org.rifidi.edge.core.readersession.service.ReaderSessionListener;
import org.rifidi.edge.core.readersession.service.ReaderSessionService;
import org.rifidi.edge.persistence.PersistenceStarterThread;
import org.rifidi.edge.persistence.service.PersistenceService;
import org.rifidi.edge.persistence.xml.PersistedReaderInfo;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * This class is the implementation of the Persistence Service interface, which
 * will be used to persist ReaderInfo objects.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class PersistanceServiceImpl implements PersistenceService,
		ReaderPluginListener, ReaderSessionListener {

	/**
	 * The log4j logger.debug.
	 */
	private static final Log logger = LogFactory
			.getLog(PersistanceServiceImpl.class);

	/**
	 * This is the default folder path
	 */
	private static final String DEFAULT_PATH = "../domains/default/";

	/**
	 * This is the default filename
	 */
	private static final String DEFAULT_FILENAME = "readerconfiguration.xml";

	/**
	 * The PersistedReaderInfo object.
	 */
	private PersistedReaderInfo pri;

	/**
	 * The readerSessionService, which we use to create reader sessions from
	 * readerInfos we retrieve from xml.
	 */
	private ReaderSessionService readerSessionService;

	/**
	 * The readerPluginService, which handles notifying the persistence layer
	 * about reader plugins which go offline and online.
	 */
	private ReaderPluginService readerPluginService;
	
	private boolean created=false;
	private Boolean createdLock = new Boolean(false); 

	/**
	 * Constructor.
	 */
	public PersistanceServiceImpl() {
		ServiceRegistry.getInstance().service(this);
		pri = new PersistedReaderInfo();
		logger.debug("created");
		created=true;
		synchronized(createdLock){
			createdLock.notify();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.persistence.service.PersistenceService#start(java.lang
	 * .String)
	 */
	@Override
	public void start(String fileName) {
		while(!created){
			synchronized (createdLock) {
				try {
					createdLock.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		logger.debug("Starting the persistence thread");
		try {
			if (fileName == null) {
				this.pri.setFile(DEFAULT_PATH, DEFAULT_PATH + DEFAULT_FILENAME);
			} else {
				/*
				 * FIXME we need to get the path as well for reasons to stupid
				 * to explain. Set to a custom path as soon as we are able!
				 */
				this.pri.setFile(DEFAULT_PATH, fileName);
			}
		} catch (IOException e) {
			// TODO: We are screwed if this happens, best to fail gracefully,
			// think of a way
			e.printStackTrace();
		}
	
		List<String> readerNameList = this.readerPluginService
				.getAllReaderInfos();
		for (String readerClassName : readerNameList) {
			List<ReaderInfo> readerInfoList = pri
					.getReaderInfos(readerClassName);
			for (ReaderInfo ri : readerInfoList) {
				this.readerSessionService.createReaderSession(ri);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.readerplugin.service.ReaderPluginListener#
	 * readerPluginRegisteredEvent(java.lang.Class)
	 */
	@Override
	public void readerPluginRegisteredEvent(
			Class<? extends ReaderInfo> readerInfo) {
		List<ReaderInfo> readerInfoList = pri.getReaderInfos(readerInfo
				.getName());
		for (ReaderInfo ri : readerInfoList) {
			this.readerSessionService.createReaderSession(ri);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.readerplugin.service.ReaderPluginListener#
	 * readerPluginUnregisteredEvent(java.lang.Class)
	 */
	@Override
	public void readerPluginUnregisteredEvent(
			Class<? extends ReaderInfo> readerInfo) {
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readersession.service.ReaderSessionListener#addEvent
	 * (org.rifidi.edge.core.readersession.ReaderSession)
	 */
	@Override
	public void addReaderSessionEvent(ReaderSession readerSession) {
		if (pri.addReaderInfo(readerSession.getReaderInfo())) {
			logger.debug("Added a reader: "
					+ readerSession.getReaderInfo().getIpAddress() + " : "
					+ readerSession.getReaderInfo().getPort());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readersession.service.ReaderSessionListener#removeEvent
	 * (org.rifidi.edge.core.readersession.ReaderSession)
	 */
	@Override
	public void removeReaderSessionEvent(ReaderSession readerSession) {
		try {
			if (pri.removeReader(readerSession.getReaderInfo()) != null) {
				logger.debug("Removed a reader: "
						+ readerSession.getReaderInfo().getIpAddress() + " : "
						+ readerSession.getReaderInfo().getPort());
			}
		} catch (RifidiReaderInfoNotFoundException e) {
			logger.debug("Cannot remove reader: " + e);
		}
	}

	@Override
	public void autoRemoveReaderSessionEvent(ReaderSession readerSession) {
		// ignore this
	}

	/**
	 * Injects the ReaderPluginService.
	 * 
	 * @param rps
	 */
	@Inject
	public void setReaderPluginService(ReaderPluginService rps) {
		this.readerPluginService = rps;
		this.readerPluginService.addReaderPluginListener(this);

		// Create and start the thread
		PersistenceStarterThread perStart = new PersistenceStarterThread(this,
				null);
		perStart.start();
	}

	/**
	 * Injects the ReaderSessionService.
	 * 
	 * @param readerSessionService
	 */
	@Inject
	public void setReaderSessionService(
			ReaderSessionService readerSessionService) {
		this.readerSessionService = readerSessionService;
		this.readerSessionService.addReaderSessionListener(this);
	}

}
