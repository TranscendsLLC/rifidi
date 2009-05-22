/* 
 *  XYLayer.java
 *  Created:	Aug 19, 2008
 *  Project:	RiFidi Dashboard - An RFID infrastructure monitoring tool
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
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
