/* 
 *  XYLayer.java
 * 
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.client.twodview.layers;

import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

/**
 * Provides a layer with improved functionality for adding images.
 * 
 * @author Tobias Hoppenthaler - tobias@pramari.com
 */
public class XYLayer extends Layer {

	/**
	 * Creates a layer with XYLayout and image handling functionality
	 */
	public XYLayer() {
		setLayoutManager(new XYLayout());

	}

	/**
	 * Adds an Image to the layer at position 0,0
	 * 
	 * @param image
	 */
	public void addImage(Image image) {
		addImage(image, 0, 0);
	}

	/**
	 * Adds an Image to the layer at position x,y
	 * 
	 * @param image
	 * @param x
	 * @param y
	 */
	public void addImage(Image image, int x, int y) {
		ImageFigure imf = new ImageFigure(image);
		imf.setBounds(new Rectangle(x, y, image.getBounds().width, image
				.getBounds().height));
		add(imf);

	}

}
