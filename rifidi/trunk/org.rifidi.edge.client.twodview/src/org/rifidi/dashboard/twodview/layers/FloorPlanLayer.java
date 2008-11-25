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
package org.rifidi.dashboard.twodview.layers;

import org.eclipse.swt.graphics.Image;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class FloorPlanLayer extends XYLayer {

	Image floorplan;

	/**
	 * @return the floorplan
	 */
	public Image getFloorplan() {
		return floorplan;
	}

	/**
	 * Makes sure that there is only one floorplan set. Disposes former
	 * floorplan and sets a new one (provided Image). If the provided Image is
	 * null, the old image is removed.
	 * 
	 * @param floorplan
	 *            the floorplan to set
	 */
	public void setFloorplan(Image floorplan) {
		removeAll();
		if (floorplan == null)
			return;
		this.floorplan = floorplan;
		addImage(floorplan);

	}

	public void setFloorplan(String absPathToImage) {
		Image floorplan = new Image(null, absPathToImage);
		setFloorplan(floorplan);

	}

}
