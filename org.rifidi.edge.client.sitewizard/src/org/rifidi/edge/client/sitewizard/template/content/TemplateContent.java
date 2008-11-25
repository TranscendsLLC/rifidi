/* 
 * TemplateContent.java
 *  Created:	Aug 11, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.sitewizard.template.content;

import java.util.List;

/**
 * The TemplateContent is a composite which allows the selection of 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class TemplateContent {

	/**
	 * 
	 */
	private String name;

	/**
	 * 
	 */
	private List<String> includeList = null;

	/**
	 * 
	 * @param temp
	 */
	public TemplateContent(String name, List<String> includeList) {
		this.name = name;

		this.includeList = includeList;
	}

	/**
	 * @return the includeList
	 */
	public List<String> getIncludeList() {
		return includeList;
	}

	/**
	 * @param includeList the includeList to set
	 */
	public void setIncludeList(List<String> includeList) {
		this.includeList = includeList;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
