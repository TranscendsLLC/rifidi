/* 
 * AbstractReaderInfoSuite.java
 *  Created:	Jun 25, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.persistence.utilities;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;

/**
 * This class holds a list of AbstractReaderInfos to write to XML.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
@XmlRootElement
public class AbstractReaderInfoSuite {

	/**
	 * The list of AbstractReaderInfos.
	 */
	private List<AbstractReaderInfo> infoList = null;

	/**
	 * The AbstractReaderInfoSuite constructor.  
	 */
	public AbstractReaderInfoSuite(List<AbstractReaderInfo> infoList) {
		this.infoList = infoList;
	}

	/**
	 * Gets the list of AbstractReaderInfos.
	 * 
	 * @return
	 */
	public List<AbstractReaderInfo> getAbstractReaderInfoList() {
		return infoList;
	}

	/**
	 * Sets the list of AbstractReaderInfos.
	 * 
	 * @param list
	 */
	public void setAbstractReaderInfoList(List<AbstractReaderInfo> list) {
		this.infoList = list;
	}
}
