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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

	public static final String XML_FILE_PATH = "../domains/default/";

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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.persistence.service.EdgePersistenceService#restore()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AbstractReaderInfo> restore() throws JAXBException {

		File f=new File("omg.txt");
		try {
			f.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		System.out.println(f.getAbsolutePath());
		
		AbstractReaderInfoSuite cs = null;
		try {
			String xml = JAXBUtility.getInstance().restoreFromFile(
					XML_FILE_PATH + XML_FILENAME);
			cs = (AbstractReaderInfoSuite) JAXBUtility.getInstance().load(xml);
		} catch (JAXBException e) {
			throw e;
		} catch (FileNotFoundException e) {
			return new ArrayList<AbstractReaderInfo>();
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
	public void readerConnectionRegistryAddEvent(AbstractReaderInfo connection) {
		// TODO Saving everything to a file, sort of hackish
		List<ReaderConnection> readConnList = rsrc.getAllReaderConnections();
		List<AbstractReaderInfo> absList = new ArrayList<AbstractReaderInfo>();
		for (ReaderConnection rc : readConnList) {
			absList.add(rc.getConnectionInfo());
		}
		this.saveToFile(absList, (XML_FILE_PATH + XML_FILENAME));
	}

	/**
	 * 
	 */
	@Override
	public void readerConnectionRegistryRemoveEvent(
			AbstractReaderInfo connection) {
		// TODO Saving everything to a file, sort of hackish
		List<ReaderConnection> readConnList = rsrc.getAllReaderConnections();
		List<AbstractReaderInfo> absList = new ArrayList<AbstractReaderInfo>();
		for (ReaderConnection rc : readConnList) {
			absList.add(rc.getConnectionInfo());
		}
		this.saveToFile(absList, (XML_FILE_PATH + XML_FILENAME));
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
		//TODO Uncomment this when we have a good solution to the persistence problem.  
		//this.initialize();
	}

	/**
	 * 
	 */
	void initialize() {
		System.out.println("Starting the initialize");
		List<ReaderConnection> readerConnections = this.rsrc
				.getAllReaderConnections();
		List<AbstractReaderInfo> currentReaders = new ArrayList<AbstractReaderInfo>();
		for (ReaderConnection rc : readerConnections) {
			currentReaders.add(rc.getConnectionInfo());
		}
		List<AbstractReaderInfo> readersFromFile;
		try {
			readersFromFile = this.restore();
		} catch (JAXBException e) {
			e.printStackTrace();
			return;
		}

		List<AbstractReaderInfo> readersToCreate = abstractReaderInfoListDiff(
				currentReaders, readersFromFile);

		for (AbstractReaderInfo a : readersToCreate) {
			System.out.println("Creating a reader: " + a);
			this.rsrc.createReaderConnection(a);
		}
	}

	/**
	 * Returns every reader in the fromFile that is not in current.
	 * 
	 * @param current
	 *            The list of AbstractReaderInfo
	 * @param fromFile
	 * @return
	 */
	private List<AbstractReaderInfo> abstractReaderInfoListDiff(
			List<AbstractReaderInfo> current, List<AbstractReaderInfo> fromFile) {
		List<AbstractReaderInfo> retVal = new ArrayList<AbstractReaderInfo>();
		for (AbstractReaderInfo a : fromFile) {
			for (AbstractReaderInfo b : current) {
				if (!a.equals(b)) {
					retVal.add(a);
				}
			}
		}
		return retVal;
	}
}
