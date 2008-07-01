/* 
 * EdgePersistenceServiceImpl.java
 *  Created:	Jun 20, 2006
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.persistence.service.impl;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.connection.impl.ReaderConnection;
import org.rifidi.edge.core.connection.listener.ReaderConnectionListener;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.service.readerconnection.ReaderConnectionRegistryService;
import org.rifidi.edge.persistence.service.EdgePersistenceService;
import org.rifidi.edge.persistence.utilities.AbstractReaderInfoSuite;
import org.rifidi.edge.persistence.utilities.JAXBUtility;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * The persistence implementation, which handles loading and saving to files.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class EdgePersistenceServiceImpl implements EdgePersistenceService,
		ReaderConnectionListener {

	public static final String XML_FILENAME = "cuddles.xml";

	/**
	 * The log4j logger.
	 */
	@SuppressWarnings("unused")
	private static Log logger = LogFactory
			.getLog(EdgePersistenceServiceImpl.class);

	/**
	 * 
	 */
	private ReaderConnectionRegistryService rsrc;

	/**
	 * 
	 */
	public EdgePersistenceServiceImpl() {
		ServiceRegistry.getInstance().service(this);
	}

	/**
	 * The filename.
	 */
	private String filename = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.persistence.service.EdgePersistenceService#save(java.util.List,
	 *      java.lang.String)
	 */
	@Override
	public void saveToFile(List<AbstractReaderInfo> connectionList,
			String filename) {
		AbstractReaderInfoSuite cs = new AbstractReaderInfoSuite(connectionList);
		JAXBUtility.getInstance().saveToFile(
				JAXBUtility.getInstance().save(cs), filename);
		this.filename = filename;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.persistence.service.EdgePersistenceService#restore()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AbstractReaderInfo> restore() {

		AbstractReaderInfoSuite cs = null;
		try {
			String xml = JAXBUtility.getInstance().restoreFromFile(filename);
			cs = (AbstractReaderInfoSuite) JAXBUtility.getInstance().load(xml);
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return cs.getAbstractReaderInfoList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.persistence.service.EdgePersistenceService#saveToString()
	 */
	@Override
	public List<String> saveToString(List<AbstractReaderInfo> connectionList) {
		List<String> strList = new ArrayList<String>();

		for (AbstractReaderInfo ari : connectionList) {
			strList.add(JAXBUtility.getInstance().save(ari));
		}
		return strList;
	}

	/**
	 * 
	 */
	@Override
	public void readerConnectionRegistryAddEvent(
			ReaderConnection connection) {
		// TODO Saving everything to a file, sort of hackish
		List<ReaderConnection> readConnList = rsrc.getAllReaderConnections();
		List<AbstractReaderInfo> absList = new ArrayList<AbstractReaderInfo>();
		for(ReaderConnection rc:readConnList) {
			absList.add(rc.getConnectionInfo());
		}
		this.saveToFile(absList, EdgePersistenceServiceImpl.XML_FILENAME);
	}

	/**
	 * 
	 */
	@Override
	public void readerConnectionRegistryRemoveEvent(
			ReaderConnection connection) {
		// TODO Saving everything to a file, sort of hackish
		List<ReaderConnection> readConnList = rsrc.getAllReaderConnections();
		List<AbstractReaderInfo> absList = new ArrayList<AbstractReaderInfo>();
		for(ReaderConnection rc:readConnList) {
			absList.add(rc.getConnectionInfo());
		}
		this.saveToFile(absList, EdgePersistenceServiceImpl.XML_FILENAME);
	}

	/**
	 * 
	 * 
	 * @param newReaderConnectionRegistryService
	 *            the newReaderConnectionRegistryService to set
	 */
	@Inject
	public void setReaderConnectionRegistryService(
			ReaderConnectionRegistryService newReaderConnectionRegistryService) {
		this.rsrc = newReaderConnectionRegistryService;
		newReaderConnectionRegistryService.addEventListener(this);
	}
}
