/* 
 *  ReaderPos.java
 *  Created:	May 11, 2009
 *  Project:	RiFidi org.rifidi.edge.client.twodview
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.twodview.util;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Holds the ID and x/y-position of a reader in a map.
 * 
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
@XmlRootElement
public class ReaderPos {
	private String ID;

	private int x, y;

	/**
	 * @return the iD
	 */
	public String getID() {
		return ID;
	}

	/**
	 * @param iD
	 *            the iD to set
	 */
	public void setID(String iD) {
		ID = iD;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x
	 *            the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y
	 *            the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}

}
