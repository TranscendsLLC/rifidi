/* 
 *  FloorPlanLayer.java
 *  Created:	Aug 19, 2008
 *  Project:	RiFidi Dashboard - An RFID infrastructure monitoring tool
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.twodview.layers;
//TODO: Comments
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.rifidi.edge.client.twodview.Activator;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class FloorPlanLayer extends XYLayer {

	public static final Dimension MAXSIZE = new Dimension(1024, 768);

	public FloorPlanLayer() {
		super();
		setMaximumSize(MAXSIZE);
	}

	/**
	 * @return the floorplan
	 */
	public Image getFloorplan() {
		if (getChildren().isEmpty())
			return null;
		ImageFigure ifig = (ImageFigure) this.getChildren().get(0);
		return ifig.getImage();
	}

	/**
	 * Makes sure that there is only one floorplan set. Disposes former
	 * floorplan and sets a new one (provided Image).
	 * 
	 * @param floorplan
	 *            the floorplan to set
	 */
	public void setFloorplan(Image floorplan) {
		removeAll();
		if (floorplan.getBounds().width > MAXSIZE.width
				|| floorplan.getBounds().height > MAXSIZE.height) {
			floorplan = resizeMap(floorplan);
		}
		addImage(floorplan);

	}

	public void setFloorplan(String absPathToImage) {
		Image floorplan = new Image(null, absPathToImage);
		setFloorplan(floorplan);
	
	}

	public void init() {
		setFloorplan(Activator.imageDescriptorFromPlugin(
				"org.rifidi.edge.client.twodview", "icons/3d_room_blueprint.jpg")
				.createImage());
	}

	private Image resizeMap(Image original) {
		/*
		 * get difference in width and height get the better suited one
		 * calculate scale factor resize by that return image
		 */
		int diffw, diffh, origw, origh;
		double factor;
		origw = original.getBounds().width;
		origh = original.getBounds().height;
		diffw = origw - MAXSIZE.width;
		diffh = origh - MAXSIZE.height;
		if (diffw > diffh) {
			factor = (double)MAXSIZE.width / (double)origw;
		} else {
			factor = MAXSIZE.height / origh;
		}

		return new Image(Display.getDefault(), original.getImageData()
				.scaledTo((int) (origw * factor), (int) (origh * factor)));
	}

}
