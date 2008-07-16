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
import org.rifidi.edge.core.readersession.service.ReaderSessionService;
import org.rifidi.edge.persistence.service.impl.PersistanceServiceImpl;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;


/**
 * 
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
	 * 
	 */
	@Test
	public void testLoading() {
		logger.debug("Starting the load test");
		 PersistanceServiceImpl perSerImpl = new PersistanceServiceImpl();

		perSerImpl.start(null);
	}
	
	/**
	 * 
	 */
	@Test
	public void testSaving() {
		logger.debug("Starting the save test");
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
