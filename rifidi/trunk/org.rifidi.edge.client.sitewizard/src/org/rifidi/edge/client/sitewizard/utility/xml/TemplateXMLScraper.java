/* 
 * TemplateXMLScraper.java
 *  Created:	Jul 28, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.sitewizard.utility.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.rifidi.edge.client.sitewizard.constants.PluginConstants;

/**
 * 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class TemplateXMLScraper {

	/**
	 * 
	 */
	private static TemplateXMLScraper instance = new TemplateXMLScraper();

	/**
	 * 
	 */
	private TemplateXMLScraper() {
	}

	/**
	 * 
	 * 
	 * @return the instance
	 */
	public static TemplateXMLScraper getInstance() {
		return instance;
	}

	/**
	 * 
	 * 
	 * @param xmlFile
	 * @return
	 */
	public List<String> getIncludeList(File xmlFile) {
		List<String> retVal = new ArrayList<String>();
		try {
			Document d = new SAXBuilder().build(xmlFile);
			Element root = d.getRootElement();
			List<Element> elementList = root.getChildren();
			for (Element e : elementList) {
				if (e.getName().equals(PluginConstants.TEMPLATE_INCLUDE)) {
					retVal.add(e.getText());
				}
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return retVal;
	}
}
