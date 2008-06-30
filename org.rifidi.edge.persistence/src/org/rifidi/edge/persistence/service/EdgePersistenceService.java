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

import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;

/**
 * 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public interface EdgePersistenceService {

	/**
	 * 
	 * @param connectionList
	 * @param filename
	 */
	public void saveToFile(List<AbstractReaderInfo> connectionList,
			String filename);

	/**
	 * 
	 * @return
	 */
	public List<AbstractReaderInfo> restore();

	/**
	 * 
	 * 
	 * @param connectionList
	 * @param filename
	 * @return
	 */
	public List<String> saveToString(List<AbstractReaderInfo> connectionList);
}
