/* 
 * ReaderObjectList.java
 *  Created:	Aug 12, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.sitewizard.reader.content;

import java.util.List;

import org.rifidi.edge.client.sitewizard.creator.ReaderObject;
import org.rifidi.edge.client.sitewizard.utility.xml.ReaderXMLScraper;


/**
 * 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class ReaderObjectList {

	/**
	 * 
	 */
	private static ReaderObjectList instance = new ReaderObjectList();

	/**
	 * 
	 */
	private ReaderObjectList() {
		
	}

	/**
	 * @return the instance
	 */
	public static ReaderObjectList getInstance() {
		return instance;
	}

	/**
	 * @return
	 */
	public ReaderObject[] getReaderObjectList() {
		List<ReaderObject> retVal = ReaderXMLScraper.getInstance().getReaderObjectList();
		return retVal.toArray(new ReaderObject[]{});
	}
	
	/**
	 * 
	 * @return
	 */
	public List<ReaderObject> getReaderObjectArrayList() {
		return ReaderXMLScraper.getInstance().getReaderObjectList();
	}
	
	/**
	 * 
	 * @return
	 */
	public String getText() {
		return "Readers";
	}
}
