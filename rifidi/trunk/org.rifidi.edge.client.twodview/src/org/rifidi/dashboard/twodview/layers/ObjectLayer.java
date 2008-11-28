/* 
 *  ObjectLayer.java
 *  Created:	Aug 19, 2008
 *  Project:	RiFidi Dashboard - An RFID infrastructure monitoring tool
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.dashboard.twodview.layers;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.rifidi.dashboard.twodview.exceptions.ReaderAlreadyInMapException;
import org.rifidi.dashboard.twodview.sfx.ReaderAlphaImageFigure;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class ObjectLayer extends XYLayer {

	// private Log logger;

	/**
	 * Creates a new Layer with MouseListener and MouseMotionListener
	 */
	public ObjectLayer() {
		super();
		ServiceRegistry.getInstance().service(this);
	}

	public void addReader(ReaderAlphaImageFigure reader, Point location)
			throws ReaderAlreadyInMapException {
		
		if (location != null) {
			reader.setBounds(new Rectangle(location.x, location.y, reader
					.getImage().getBounds().width, reader.getImage()
					.getBounds().height));
		}
		if (this.getChildren().contains(reader)) {
			throw new ReaderAlreadyInMapException("Drag and Drop failed");

		} else{
			add(reader);
			return;
		}
			

	}

}
