/* 
 * TemplateContentList.java
 *  Created:	Aug 11, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.sitewizard.template.content;

import java.io.File;

import org.rifidi.edge.client.sitewizard.utility.xml.TemplateXMLScraper;

/**
 * @author Matthew Dean - matt@pramari.com
 * 
 */
public class TemplateContentList {

	public final static File CORE_XML_FILE = new File("core.xml");

	public final static TemplateContent CORE = new TemplateContent(
			"CORE_TEMPLATE", TemplateXMLScraper.getInstance().getIncludeList(
					CORE_XML_FILE));

	public final static String CORE_FILE_NOT_FOUND = "Could not find \"core.xml\" in folder /utilities/eclipse";

	/**
	 * 
	 */
	private static TemplateContentList instance = new TemplateContentList();

	/**
	 * 
	 */
	private TemplateContentList() {
		if (!CORE_XML_FILE.exists()) {

		}
	}

	/**
	 * @return the instance
	 */
	public static TemplateContentList getInstance() {
		return instance;
	}

	/**
	 * @return
	 */
	public TemplateContent[] getTemplateList() {
		TemplateContent[] retVal = new TemplateContent[] { CORE };
		return retVal;
	}
}
