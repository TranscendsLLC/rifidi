/*
 *  LLRPReaderAdapterTest.java
 *
 *  Created:	Jun 20, 2006
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.adapter.llrp;

import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.common.utilities.converter.ByteAndHexConvertingUtility;
import org.rifidi.edge.core.exception.readerPlugin.RifidiConnectionException;
import org.rifidi.edge.core.tag.TagRead;
import org.rifidi.edge.readerPlugin.llrp.LLRPReaderInfo;
import org.rifidi.edge.readerPlugin.llrp.LLRPReaderPlugin;

/**
 * LLRP reader adapter.  
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class LLRPReaderAdapterTest extends TestCase {

	/**
	 * The log4j logger
	 */
	private static Log logger = LogFactory.getLog(LLRPReaderAdapterTest.class);

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
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
			newAdapt.connect();
		} catch (RifidiConnectionException e) {
			Assert.fail();
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
			newAdapt.connect();
		} catch (RifidiConnectionException e) {
			e.printStackTrace();
			Assert.fail();
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
	///public void testRawCommand() {
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
			newAdapt.connect();
		} catch (RifidiConnectionException e) {
			Assert.fail();
		}

		try {
			newAdapt.disconnect();
		} catch (RifidiConnectionException e) {
			Assert.fail();
		}
	}
}
