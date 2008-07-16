/* 
 * PersistenceTestClass.java
 *  Created:	Jul 15, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.testing.persistence;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.core.readersession.ReaderSession;
import org.rifidi.edge.core.readersession.service.ReaderSessionService;
import org.rifidi.edge.persistence.service.impl.PersistanceServiceImpl;
import org.rifidi.edge.readerplugin.alien.AlienReaderInfo;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;


/**
 * This class tests the persistence layer, and other fun things.  
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class PersistenceTestClass {
	
	private ReaderSessionService readerSessionService;

	/**
	 * The log4j logger.debug.
	 */
	private static final Log logger = LogFactory.getLog(PersistenceTestClass.class);
	
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
	 * Test the loading of the reader.  
	 */
	@Test
	public void testLoading() {
		logger.debug("Starting the load test");
		 PersistanceServiceImpl perSerImpl = new PersistanceServiceImpl();

		perSerImpl.start(null);
	}
	
	/**
	 * Tests the saving part of the persistence test class, including the 
	 */
	@Test
	public void testSaving() {
		AlienReaderInfo ari = new AlienReaderInfo();
		ari.setIpAddress("192.168.1.107");
		ari.setPort(40000);
		ari.setPassword("omgzor");
		ari.setUsername("alienzor");
		ReaderSession rs = readerSessionService.createReaderSession(ari);
		
		readerSessionService.destroyReaderSession(rs);
		
		logger.debug("Starting the save test");
	}
	
	/**
	 * Injects the ReaderSessionService.  
	 * 
	 * @param service
	 */
	@Inject
	public void setReaderSessionService(ReaderSessionService service) {
		readerSessionService = service;
	}
}
