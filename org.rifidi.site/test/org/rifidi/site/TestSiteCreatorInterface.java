/* 
 * TestSiteCreatorInterface.java
 *  Created:	Jul 29, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.site;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * @author Matthew Dean - matt@pramari.com
 *
 */
public class TestSiteCreatorInterface {

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
	public void TestCreateBatchFile() {
		File f = new File("omg");
		System.out.println(f.getAbsolutePath());
	}
}
