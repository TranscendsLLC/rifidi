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
package org.rifidi.edge.persistence.service;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.persistence.utilities.AbstractReaderInfoSuite;
import org.rifidi.edge.persistence.utilities.JAXBUtility;

/**
 * The persistence
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class EdgePersistenceServiceImpl implements EdgePersistenceService {

	/**
	 * The log4j logger.
	 */
	@SuppressWarnings("unused")
	private static Log logger = LogFactory
			.getLog(EdgePersistenceServiceImpl.class);

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
			cs = (AbstractReaderInfoSuite) JAXBUtility.getInstance().load(xml,
					AbstractReaderInfoSuite.class);
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
}
