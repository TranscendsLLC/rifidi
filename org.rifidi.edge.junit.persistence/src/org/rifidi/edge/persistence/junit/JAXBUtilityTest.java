/* 
 * JAXBUtilityTest.java
 *  Created:	Jun 20, 2006
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.persistence.junit;

import static org.junit.Assert.fail;

import javax.xml.bind.JAXBException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.persistence.utilities.JAXBUtility;
import org.rifidi.edge.readerPlugin.alien.AlienReaderInfo;

/**
 * Test class for JAXBUtility.  
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class JAXBUtilityTest {

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
	 * Test load the 
	 */
	@Test
	public void testLoad() {
		AlienReaderInfo omg = new AlienReaderInfo();
		
		omg.setIPAddress("192.168.1.100");
		omg.setPort(23);
		omg.setUsername("alien");
		omg.setPassword("password");
		
		AbstractReaderInfo ari = omg;
		
		try {
			String lol = JAXBUtility.getInstance().save(ari);
			JAXBUtility.getInstance().load(lol,AlienReaderInfo.class);
		} catch (JAXBException e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Test save the object.  
	 */
	@Test
	public void testSave() {
		AlienReaderInfo omg = new AlienReaderInfo();
		
		omg.setIPAddress("192.168.1.100");
		omg.setPort(23);
		omg.setUsername("alien");
		omg.setPassword("password");
		
		AbstractReaderInfo ari = omg;
		
		try {
			String lol = JAXBUtility.getInstance().save(ari);
			JAXBUtility.getInstance().load(lol,AlienReaderInfo.class);
		} catch (JAXBException e) {
			e.printStackTrace();
			fail();
		}
	}

}
