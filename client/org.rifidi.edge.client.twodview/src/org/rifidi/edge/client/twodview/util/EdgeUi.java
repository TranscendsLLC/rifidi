/* 
 *  EdgeUi.java
 * 
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.client.twodview.util;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Holds the background image and the positions of the readers in the map.
 * 
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
@XmlRootElement
public class EdgeUi {
	/** path to the floorplan */
	private String pathToImageFile;
	/** scale factor of the scalable layered pane */
	private double scaleFactor;
	/** x offset of the map */
	private int xOffset;
	/** y offset of the map */
	private int yOffset;
	/** Reader positions and ids */
	private Set<ReaderPos> readerPositions;

	/**
	 * @return the pathToImageFile
	 */
	public String getPathToImageFile() {
		return pathToImageFile;
	}

	/**
	 * @param pathToImageFile
	 *            the pathToImageFile to set
	 */
	public void setPathToImageFile(String pathToImageFile) {
		this.pathToImageFile = pathToImageFile;
	}

	/**
	 * @return the scaleFactor
	 */
	public double getScaleFactor() {
		return scaleFactor;
	}

	/**
	 * @param scaleFactor
	 *            the scaleFactor to set
	 */
	public void setScaleFactor(double scaleFactor) {
		this.scaleFactor = scaleFactor;
	}

	/**
	 * @return the xOffset
	 */
	public int getxOffset() {
		return xOffset;
	}

	/**
	 * @param xOffset
	 *            the xOffset to set
	 */
	public void setxOffset(int xOffset) {
		this.xOffset = xOffset;
	}

	/**
	 * @return the yOffset
	 */
	public int getyOffset() {
		return yOffset;
	}

	/**
	 * @param yOffset
	 *            the yOffset to set
	 */
	public void setyOffset(int yOffset) {
		this.yOffset = yOffset;
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
