
package org.rifidi.edge.client.twodview.sfx;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.swt.graphics.Image;

/**
 * Extends the capabilities of an ImageFigure with the ability to change the alpha value.
 * 
 * @author Tobias Hoppenthaler - tobias@pramari.com
 */
public class AlphaImageFigure extends ImageFigure {

	/** The alpha value for this Image */
	private int alpha = 255;

	/**
	 * Constructor
	 * 
	 * @param image
	 */
	public AlphaImageFigure(Image image) {
		super(image);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.draw2d.ImageFigure#paintFigure(org.eclipse.draw2d.Graphics)
	 */
	@Override
	protected void paintFigure(Graphics graphics) {
		graphics.setAlpha(alpha);
		super.paintFigure(graphics);
	}

	/**
	 * @return alpha - current alpha value of the image
	 */
	public int getAlpha() {
		return alpha;
	}

	/**
	 * @param alpha
	 *            - set the alpha value of the image
	 */
	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}

	/**
	 * Should be called when we get rid of this AlphaImageFigure. Subclasses
	 * should override this method and call super.dispose(). Suclasses should
	 * also take care to dispose of the image if necessary
	 */
	public void dispose() {
	}

}
