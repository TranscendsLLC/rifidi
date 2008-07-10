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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.core.exceptions.RifidiCommandInterruptedException;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.readersession.ReaderSession;
import org.rifidi.edge.core.readersession.service.ReaderSessionService;
import org.rifidi.edge.readerplugin.alien.AlienReaderInfo;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * This class tests the Alien's connect and TagRead functionality.  
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class AlienTestTagRead {

	private ReaderSessionService readerSessionService;

	private AlienReaderInfo info;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		ServiceRegistry.getInstance().service(this);
		info = new AlienReaderInfo();
		info.setIpAddress("129.168.1.100");
		info.setPort(23);
		info.setUsername("alien");
		info.setPassword("password");
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
	public void testConnectAndDisconnect() {
		
	}

	/**
	 * 
	 */
	@Test
	public void testTagRead() {
		ReaderSession readerSession = readerSessionService.createReaderSesssion(info);
		try {
			readerSession.executeCommand("getTagList");
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
