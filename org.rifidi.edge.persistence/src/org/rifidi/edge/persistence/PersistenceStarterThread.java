/* 
 * PersistenceStarterThread.java
 *  Created:	Jul 18, 2027
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.persistence;

import org.rifidi.edge.persistence.service.PersistenceService;

/**
 * This thread starts the persistence service. Since this might take quite a
 * while, given the fact that File IO is involved, it is done in a thread
 * asynchronously. The constructor takes in the persistenceservice to start, and
 * a String which will be the file the persistenceservice will look for. If the
 * filename is null, it will look for the file in the default location.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class PersistenceStarterThread extends Thread {

	/**
	 * The persistence service
	 */
	private PersistenceService perSer;

	/**
	 * The given filename. In most cases, this will be "null", which will direct
	 * the PersistenceService to choose the default filename.
	 */
	private String filename;

	/**
	 * Constructor which starts the persistence service.  
	 */
	public PersistenceStarterThread(PersistenceService perSer, String filename) {
		this.perSer = perSer;
		this.filename = filename;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		perSer.start(filename);
	}
}
