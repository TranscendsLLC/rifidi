/*
 *  LLRPReaderAdapterTest.java
 *
 *  Created:	Jun 19, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerplugin.llrp.junit;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.common.utilities.converter.ByteAndHexConvertingUtility;
import org.rifidi.edge.core.communication.buffer.ConnectionBuffer;
import org.rifidi.edge.core.communication.service.CommunicationService;
import org.rifidi.edge.core.communication.service.impl.CommunicationServiceImpl;
import org.rifidi.edge.core.exception.readerConnection.RifidiConnectionException;
import org.rifidi.edge.core.tag.TagRead;
import org.rifidi.edge.readerPlugin.llrp.LLRPProtocol;
import org.rifidi.edge.readerPlugin.llrp.LLRPReaderInfo;
import org.rifidi.edge.readerPlugin.llrp.LLRPReaderPlugin;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * LLRP reader adapter.  
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class LLRPReaderPluginTest extends TestCase {

	private CommunicationService commSer;
	
	/**
	 * The log4j logger.
	 */
	private static Log logger = LogFactory.getLog(LLRPReaderPluginTest.class);

	/**
	 * Sets up the TestCase.  
	 * 
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		commSer = new CommunicationServiceImpl();
		ServiceRegistry.getInstance().service(this);
	}

	/**
	 * Tears down the TestCase.  
	 * 
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests connecting the reader
	 */
	@Test
	public void testConnect() {
		LLRPReaderInfo lci = new LLRPReaderInfo();
		lci.setIPAddress("127.0.0.1");
		lci.setPort(5084);
		LLRPReaderPlugin newAdapt = new LLRPReaderPlugin(lci);
		try {
			ConnectionBuffer omg = commSer.createConnection(newAdapt,
					 lci, new LLRPProtocol());
			newAdapt.connect(omg);
		} catch (RifidiConnectionException e) {
			Assert.fail();
		} catch (ConnectException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			newAdapt.disconnect();
		} catch (RifidiConnectionException e) {
			Assert.fail();
		}
	}

	/**
	 * Tests the tag streaming portion of the code
	 */
	@Test
	public void testStream() {
		LLRPReaderInfo lci = new LLRPReaderInfo();
		lci.setIPAddress("127.0.0.1");
		lci.setPort(5084);
		LLRPReaderPlugin newAdapt = new LLRPReaderPlugin(lci);

		try {
			ConnectionBuffer omg = commSer.createConnection(newAdapt,
					 lci, new LLRPProtocol());
			newAdapt.connect(omg);
		} catch (RifidiConnectionException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (ConnectException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<TagRead> newTags = newAdapt.getNextTags();
		
		

		for (TagRead t : newTags) {
			logger.debug("Tag: "
					+ ByteAndHexConvertingUtility.toHexString(t.getId()));
		}

		try {
			newAdapt.disconnect();
		} catch (RifidiConnectionException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	/**
	 * Tests the raw command sending to the reader
	 */
	//public void testRawCommand() {
	//	Assert.fail();
	//}

	/**
	 * Tests disconnecting the reader
	 */
	@Test
	public void testDisconnect() {
		logger.debug("testing the disconnect");
		
		LLRPReaderInfo lci = new LLRPReaderInfo();
		lci.setIPAddress("127.0.0.1");
		lci.setPort(5084);
		LLRPReaderPlugin newAdapt = new LLRPReaderPlugin(lci);
		 try {
			 ConnectionBuffer omg = commSer.createConnection(newAdapt,
					 lci, new LLRPProtocol());
			newAdapt.connect(omg);
		} catch (RifidiConnectionException e) {
			Assert.fail();
		} catch (ConnectException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			newAdapt.disconnect();
		} catch (RifidiConnectionException e) {
			Assert.fail();
		}
	}
}
