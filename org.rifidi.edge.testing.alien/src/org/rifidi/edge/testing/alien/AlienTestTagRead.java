/* 
 * AlienTestTagRead.java
 *  Created:	Jul 10, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.testing.alien;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.core.exceptions.RifidiCommandInterruptedException;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.readersession.ReaderSession;
import org.rifidi.edge.core.readersession.service.ReaderSessionService;
import org.rifidi.edge.readerplugin.alien.AlienConnectionManager;
import org.rifidi.edge.readerplugin.alien.AlienReaderInfo;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * This class tests the Alien's connect and TagRead functionality.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class AlienTestTagRead {

	private static final Log logger = LogFactory
			.getLog(AlienConnectionManager.class);

	private ReaderSessionService readerSessionService;

	private AlienReaderInfo info;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		ServiceRegistry.getInstance().service(this);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * 
	 */
	@Test
	public void testTagRead() {
		info = new AlienReaderInfo();
		info.setIpAddress("192.168.1.100");
		info.setPort(23);
		info.setUsername("alien");
		info.setPassword("password");
		info.setMaxNumConnectionsAttemps(3);
		info.setReconnectionIntervall(1000);
		
		logger.debug("Testing the tag reads");
		
		ReaderSession readerSession = readerSessionService
				.createReaderSession(info);
		
		try {
			logger.debug("Testing the getTagList command");
			readerSession.executeCommand("getTagList",null);
			
			readerSession.stopCurCommand(false);
		} catch (RifidiConnectionException e) {
			throw new AssertionError(e);
		} catch (RifidiCommandInterruptedException e) {
			throw new AssertionError(e);
		}
	}

	/**
	 * 
	 * @param service
	 */
	@Inject
	public void setReaderSessionService(ReaderSessionService service) {
		readerSessionService = service;
	}
}
