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

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.llrp.ltk.exceptions.InvalidLLRPMessageException;
import org.llrp.ltk.generated.messages.DELETE_ROSPEC;
import org.llrp.ltk.generated.messages.ENABLE_ROSPEC;
import org.llrp.ltk.types.LLRPMessage;
import org.llrp.ltk.types.UnsignedInteger;
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
	 * Tests the toObject method in LLRPProtocol
	 */
	@Test
	public void testToObject() {
		DELETE_ROSPEC deleteROSpec = new DELETE_ROSPEC();
		deleteROSpec.setROSpecID(new UnsignedInteger(1));

		ENABLE_ROSPEC enableROSpec = new ENABLE_ROSPEC();
		enableROSpec.setROSpecID(new UnsignedInteger(1));

		List<Object> arrList = new ArrayList<Object>();
		arrList.add(deleteROSpec);
		arrList.add(enableROSpec);

		try {
			byte[] ds = deleteROSpec.encodeBinary();
			byte[] es = enableROSpec.encodeBinary();
			byte[] acc = new byte[ds.length + es.length];
			int index = 0;
			for (byte b : ds) {
				acc[index] = b;
				index++;
			}
			for (byte b : es) {
				acc[index] = b;
				index++;
			}

			LLRPProtocol newProt = new LLRPProtocol();
			List<Object> newArr = newProt.toObject(acc);

			for (Object o : newArr) {
				if (((LLRPMessage) o).getClass() == DELETE_ROSPEC.class
						|| ((LLRPMessage) o).getClass() == ENABLE_ROSPEC.class) {
					logger.debug("Message checks out");
				} else {
					Assert.fail();
				}
			}

		} catch (InvalidLLRPMessageException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	/**
	 * Tests the fromObject method in LLRPProtocol
	 */
	@Test
	public void testFromObject() {
		DELETE_ROSPEC deleteROSpec = new DELETE_ROSPEC();
		deleteROSpec.setROSpecID(new UnsignedInteger(1));

		LLRPProtocol newProt = new LLRPProtocol();

		byte omg[] = newProt.fromObject(deleteROSpec);

		if (omg != null) {
			try {
				if (!byteArrEquals(omg, deleteROSpec.encodeBinary())) {
					Assert.fail();
				}
			} catch (InvalidLLRPMessageException e) {
				e.printStackTrace();
				Assert.fail();
			}
		} else {
			logger.debug("fromObject is null");
			Assert.fail();
		}
	}

	/**
	 * Checks the equality of 2 byte arrays
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private boolean byteArrEquals(byte[] a, byte[] b) {
	
		if (a.length != b.length) {
			return false;
		}

		for (int i = 0; i < a.length; i++) {
			if (a[i] != b[i]) {
				return false;
			}
		}

		return true;
	}
}
