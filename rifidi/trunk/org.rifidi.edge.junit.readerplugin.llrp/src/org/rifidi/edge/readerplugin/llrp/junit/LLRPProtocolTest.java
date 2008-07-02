/* 
 * LLRPProtocolTest.java
 *  Created:	Jun 20, 2006
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerplugin.llrp.junit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.llrp.ltk.exceptions.InvalidLLRPMessageException;
import org.llrp.ltk.generated.messages.ENABLE_ROSPEC;
import org.llrp.ltk.types.UnsignedInteger;
import org.rifidi.edge.core.exception.readerConnection.RifidiInvalidMessageFormat;
import org.rifidi.edge.readerPlugin.llrp.LLRPProtocol;

/**
 * Tests the LLRPProtocol methods.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class LLRPProtocolTest {

	/**
	 * The log4j logger.
	 */
	private static final Log logger = LogFactory.getLog(LLRPProtocolTest.class);

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
	 * 
	 */
	@Test
	public void testToByteArray() {
		ENABLE_ROSPEC enableROSpec = new ENABLE_ROSPEC();
		enableROSpec.setROSpecID(new UnsignedInteger(1));

		LLRPProtocol prot = new LLRPProtocol();

		try {
			byte[] byteArray = prot.toByteArray(enableROSpec);

			Assert.assertArrayEquals(enableROSpec.encodeBinary(), byteArray);

		} catch (RifidiInvalidMessageFormat e) {
			Assert.fail();
			e.printStackTrace();
		} catch (InvalidLLRPMessageException e) {
			Assert.fail();
			e.printStackTrace();
		}

	}

	/**
	 * 
	 */
	@Test
	public void testAddByte() {
		ENABLE_ROSPEC enableROSpec = new ENABLE_ROSPEC();
		enableROSpec.setROSpecID(new UnsignedInteger(1));

		LLRPProtocol prot = new LLRPProtocol();

		try {
			byte[] omg = enableROSpec.encodeBinary();

			Object test = null;

			for (byte b : omg) {
				test = prot.add(b);
			}

			Assert.assertNotNull(test);
			if (!(test instanceof ENABLE_ROSPEC)) {
				Assert.fail();
			}

		} catch (InvalidLLRPMessageException e) {
			logger.debug("Invalid message");
			Assert.fail();
			e.printStackTrace();
		} catch (RifidiInvalidMessageFormat e) {
			logger.debug("Invalid message format");
			Assert.fail();
			e.printStackTrace();
		}

	}

}
