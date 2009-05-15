/* 
 *  EdgeUi.java
 *  Created:	May 11, 2009
 *  Project:	RiFidi org.rifidi.edge.client.twodview
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.twodview.util;

import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Holds the background image and the positions of the readers in the map.
 * 
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
@XmlRootElement
public class EdgeUi {

	
	private String imgUrl;
	
	private Set<ReaderPos> readerPositions;

	/**
	 * @return the imgUrl
	 */
	public String getImgUrl() {
		return imgUrl;
	}

	/**
	 * @param imgUrl
	 *            the imgUrl to set
	 */
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	/**
	 * @return the readerPositions
	 */
	public Set<ReaderPos> getReaderPositions() {
		return readerPositions;
	}

	/**
	 * @param readerPositions
	 *            the readerPositions to set
	 */
	public void setReaderPositions(Set<ReaderPos> readerPositions) {
		this.readerPositions = readerPositions;
	}

}
