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
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.site.xml.ReaderXMLScraper;
import org.rifidi.site.xml.TemplateXMLScraper;

/**
 * 
 * 
 * @author Matthew Dean - matt@pramari.com
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
	public void testReaderObjectList() {
		List<ReaderObject> readerObjectList = ReaderXMLScraper.getInstance()
				.getReaderObjectList();
		for (ReaderObject ro : readerObjectList) {
			System.out.println(ro.toString());
		}
	}

	/**
	 * 
	 */
	@Test
	public void testTemplateList() {
		File f = new File("../config.xml");
		if (f.exists()) {
			List<String> templateList = TemplateXMLScraper.getInstance()
					.getIncludeList(f);
			for (String s : templateList) {
				System.out.println(s);
			}
		} else {
			Assert.fail();
		}
	}

	/**
	 * 
	 */
	@Test
	public void testSiteCreator() {
		List<String> templateList = TemplateXMLScraper.getInstance()
				.getIncludeList(new File("../config.xml"));
		List<ReaderObject> readerObjectList = ReaderXMLScraper.getInstance()
				.getReaderObjectList();

		SiteCreatorInterface.getInstance().createBatchFile("cuddles");
		SiteCreatorInterface.getInstance().createConfigFile("cuddles",
				readerObjectList, templateList);
		
		
	}
}
