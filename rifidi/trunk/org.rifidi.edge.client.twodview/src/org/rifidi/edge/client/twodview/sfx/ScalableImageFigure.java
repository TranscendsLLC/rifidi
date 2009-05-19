
package org.rifidi.edge.client.twodview.sfx;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.ScalableFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

/**
 * 
 * 
 * @author tobias
 */
public class ScalableImageFigure extends ImageFigure implements ScalableFigure {

	private double scale = 1.0;
	private Dimension imageSize;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.draw2d.ScalableFigure#getScale()
	 */
	@Override
	public double getScale() {
		return scale;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.draw2d.ScalableFigure#setScale(double)
	 */
	@Override
	public void setScale(double arg0) {
		this.scale = arg0;

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.draw2d.ImageFigure#paintFigure(org.eclipse.draw2d.Graphics)
	 */
	@Override
	protected void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);

		if (getImage() == null)
			return;
		Rectangle area = getClientArea();
		Rectangle destination = new Rectangle();

		double dw = (double) imageSize.width / (double) area.width;
		double dh = (double) imageSize.height / (double) area.height;

		if (dw < 1 && dh < 1) {
			// image is smaller than the figure bounds
			destination.width = imageSize.width;
			destination.height = imageSize.height;
		} else if (dw > dh) {
			// limit the size by width
			destination.width = area.width;
			destination.height = (int) (imageSize.height / dw);
		} else {
			// limit size by height
			destination.width = (int) (imageSize.width / dh);
			destination.height = area.height;
		}
		destination.x = (area.width - destination.width) / 2 + area.x;
		destination.y = (area.height - destination.height) / 2 + area.y;

		graphics.drawImage(getImage(), new Rectangle(getImage().getBounds()),
				destination);
		// graphics.drawRectangle(area.crop(new Insets(0,0,1,1)));
	}

	/**
	 * Constructor.  
	 * 
	 * @param image
	 */
	public ScalableImageFigure(Image image) {
		super(image);
		this.setImage(image);

	}

}
