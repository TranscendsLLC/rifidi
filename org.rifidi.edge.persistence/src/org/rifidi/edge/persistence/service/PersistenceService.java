/* 
 * PersistenceService.java
 *  Created:	Jun 20, 2006
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.persistence.service;

/**
 * This class is the interface which will handle starting the persistence
 * service.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public interface PersistenceService {

	/**
	 * Start the service. The string fileName is an absolute path to the file
	 * you want to save this configuration as. If fileName is null, it will be
	 * saved under the default domain.
	 * 
	 * @param fileName
	 */
	public void start(String fileName);

}
