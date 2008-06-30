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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.persistence.service.EdgePersistenceServiceImpl;
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
			JAXBUtility.getInstance().load(lol, AlienReaderInfo.class);
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
			JAXBUtility.getInstance().load(lol, AlienReaderInfo.class);

		} catch (JAXBException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testSaveToFile() {
		AlienReaderInfo omg = new AlienReaderInfo();

		omg.setIPAddress("192.168.1.100");
		omg.setPort(23);
		omg.setUsername("alien");
		omg.setPassword("password");

		AlienReaderInfo omg2 = new AlienReaderInfo();

		omg2.setIPAddress("192.168.1.199");
		omg2.setPort(14324);
		omg2.setUsername("omgzor");
		omg2.setPassword("lolzor");

		List<AbstractReaderInfo> aril = new ArrayList<AbstractReaderInfo>();

		aril.add(omg);
		aril.add(omg2);

		EdgePersistenceServiceImpl impl = new EdgePersistenceServiceImpl();

		List<AbstractReaderInfo> restoril = null;

		impl.saveToFile(aril, "omgzor");
		restoril = impl.restore();

		for (AbstractReaderInfo abs : restoril) {
			System.out.println(abs.toString());
		}
	}

}
