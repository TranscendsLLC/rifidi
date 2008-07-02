/* 
 * EdgePersistenceService.java
 *  Created:	Jun 20, 2006
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.persistence.service;

import java.util.List;

import javax.xml.bind.JAXBException;

import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;

/**
 * Save the edge persistence service 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public interface EdgePersistenceService {

	/**
	 * Saves the given list of AbstractReaderInfos to a file with the given filename.  
	 * 
	 * @param connectionList
	 * @param filename
	 */
	public void saveToFile(List<AbstractReaderInfo> connectionList,
			String filename);

	/**
	 * Restores the List of AbstractReaderInfos from an xml file.  
	 * 
	 * @return
	 */
	public List<AbstractReaderInfo> restore() throws JAXBException;

	/**
	 * Saves a list of AbstractReaderInfos to an XML string.  
	 * 
	 * @param connectionList
	 * @param filename
	 * @return
	 */
	public List<String> saveToString(List<AbstractReaderInfo> connectionList);
}
