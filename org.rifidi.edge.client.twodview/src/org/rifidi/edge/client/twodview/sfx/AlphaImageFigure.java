/* 
 *  AlphaImageFigure.java
 *  Created:	Aug 19, 2008
 *  Project:	RiFidi Dashboard - An RFID infrastructure monitoring tool
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.twodview.sfx;
//TODO: Comments
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.swt.graphics.Image;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
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
