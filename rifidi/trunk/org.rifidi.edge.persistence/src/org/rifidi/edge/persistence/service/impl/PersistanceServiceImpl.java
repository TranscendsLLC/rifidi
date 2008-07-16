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

	private static final String DEFAULT_PATH = "../domains/default/";

	private static final String DEFAULT_FILENAME = "readerconfiguration.xml";

	/**
	 * The PersistedReaderInfo object.
	 */
	private PersistedReaderInfo pri;

	/**
	 * The readerSessionService
	 */
	private ReaderSessionService readerSessionService;

	/**
	 * 
	 */
	private ReaderPluginService readerPluginService;

	/**
	 * Constructor.
	 */
	public PersistanceServiceImpl() {
		ServiceRegistry.getInstance().service(this);
		pri = new PersistedReaderInfo();
		logger.debug("created");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.service.ReaderPluginListener#readerPluginRegisteredEvent(java.lang.Class)
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
	 * @see org.rifidi.edge.core.readerplugin.service.ReaderPluginListener#readerPluginUnregisteredEvent(java.lang.Class)
	 */
	@Override
	public void readerPluginUnregisteredEvent(
			Class<? extends ReaderInfo> readerInfo) {
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readersession.service.ReaderSessionListener#addEvent(org.rifidi.edge.core.readersession.ReaderSession)
	 */
	@Override
	public void addEvent(ReaderSession readerSession) {
		logger.debug("ADDING A READER");
		pri.addReaderInfo(readerSession.getReaderInfo());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readersession.service.ReaderSessionListener#removeEvent(org.rifidi.edge.core.readersession.ReaderSession)
	 */
	@Override
	public void removeEvent(ReaderSession readerSession) {
		logger.debug("REMOVING A READER");
		try {
			pri.removeReader(readerSession.getReaderInfo());
		} catch (RifidiReaderInfoNotFoundException e) {
			// TODO: Should we do anything else here? If this happens something
			// seriously went wrong? We should think about this.
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.persistence.service.PersistenceService#start(java.lang.String)
	 */
	@Override
	public void start(String fileName) {
		logger.debug("Starting the persistence service");
		try {
			if (fileName == null) {
				logger.debug("Using the default xml file");
				this.pri.setFile(DEFAULT_PATH + DEFAULT_FILENAME);
			} else {
				this.pri.setFile(fileName);
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

	/**
	 * Injects the ReaderPluginService.
	 * 
	 * @param rps
	 */
	@Inject
	public void setReaderPluginService(ReaderPluginService rps) {
		this.readerPluginService = rps;
		this.readerPluginService.addReaderPluginListener(this);
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
