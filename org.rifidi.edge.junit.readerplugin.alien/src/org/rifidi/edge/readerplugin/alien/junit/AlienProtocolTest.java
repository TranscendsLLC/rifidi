/* 
 * AlienProtocolTest.java
 *  Created:	Jun 20, 2006
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerplugin.alien.junit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.core.exception.readerConnection.RifidiInvalidMessageFormat;
import org.rifidi.edge.readerPlugin.alien.AlienProtocol;

/**
 * @author Matthew Dean - matt@pramari.com
 * 
 */
public class AlienProtocolTest {

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
	 * Test Test method for
	 * {@link org.rifidi.edge.readerPlugin.alien.AlienProtocol#toObject(byte[])}.
	 */
	@Test
	public void testToObject() {
		try {
			AlienProtocol newProt = new AlienProtocol();
			String retVal = "get TagList\n\r\0";
			char[] charArray = retVal.toCharArray();
			Object omg = null;
			for (char a : charArray) {
				omg = newProt.add((byte) a);
			}

			Assert.assertNotNull(omg);
			Assert.assertEquals(retVal, (String) omg);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	/**
	 * Test Test method for
	 * {@link org.rifidi.edge.readerPlugin.alien.AlienProtocol#fromObject(java.lang.Object)}.
	 */
	@Test
	public void testToByteArray() {
		AlienProtocol newProt = new AlienProtocol();
		String retVal = "get TagList";
		try {
			if (!retVal.equals(new String(newProt.toByteArray(retVal)))) {
				Assert.fail();
			}
		} catch (RifidiInvalidMessageFormat e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

}
