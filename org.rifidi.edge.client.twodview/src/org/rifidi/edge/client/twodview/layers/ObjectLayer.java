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
package org.rifidi.edge.client.twodview.layers;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.rifidi.edge.client.twodview.exceptions.ReaderAlreadyInMapException;
import org.rifidi.edge.client.twodview.sfx.ReaderAlphaImageFigure;
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

		// FIXME: bug #40
		if (!this.getChildren().isEmpty()) {

			for (Object fig : this.getChildren()) {
				ReaderAlphaImageFigure raif = (ReaderAlphaImageFigure) fig;
				if (raif.getReaderId().equals(reader.getReaderId())) {
					throw new ReaderAlreadyInMapException(
							"Drag and Drop failed");

				}
			}
			add(reader);
			return;

		} else {
			add(reader);
			return;
		}

	}

	public void refreshObjects() {
		ReaderAlphaImageFigure raif;
		/*
		 * hand over layer; for each child call status refresh
		 */
		for (Object figure : getChildren()) {
			try {
				raif = (ReaderAlphaImageFigure) figure;
				raif.updateStatus();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}
