
package org.rifidi.edge.client.twodview.layers;

import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.rifidi.edge.client.twodview.Activator;

/**
 * The layer which contains the floorplan image in the twodview.
 * 
 * @author Tobias Hoppenthaler - tobias@pramari.com
 */
public class FloorPlanLayer extends XYLayer {

	/** Defines the maximum size of images for the floorplan - bigger images will be resized to that size or smaller (proportional) */
	public static final Dimension MAXSIZE = new Dimension(1024, 768);
	private String floorPlanImageFile="";

	/**
	 * @return the path to the floorPlanImageFile
	 */
	public String getFloorPlanImageFile() {
		return floorPlanImageFile;
	}

	/**
	 * The constructor.
	 */
	public FloorPlanLayer() {
		super();
		setMaximumSize(MAXSIZE);
	}

	/**
	 * @return the floorplan image
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
	private void setFloorplan(Image floorplan) {
		removeAll();
		if (floorplan.getBounds().width > MAXSIZE.width
				|| floorplan.getBounds().height > MAXSIZE.height) {
			floorplan = resizeMap(floorplan);
		}
		addImage(floorplan);

	}

	/**
	 * Sets the floorplan and takes in the absolute path to the image file.
	 * @param absPathToImage
	 */
	public void setFloorplan(String absPathToImage) {
		Image floorplan = new Image(null, absPathToImage);
		setFloorplan(floorplan);
		floorPlanImageFile=absPathToImage;
	
	}
/**
 * Initializes the layer with an image.
 */
	public void init() {
		setFloorplan(Activator.imageDescriptorFromPlugin(
				"org.rifidi.edge.client.twodview", "icons/3d_room_blueprint.jpg")
				.createImage());
	}
	
/**
 * This method gets called if the image exceeds MAXSIZE. The resulting image will fit the MAXSIZE requirements.
 * @param original image
 * @return resized image
 */
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
