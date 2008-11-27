/* 
 *  ListeningScalableLayeredPane.java
 *  Created:	Nov 27, 2008
 *  Project:	RiFidi Edge Client
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.dashboard.twodview.layers;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.ScalableLayeredPane;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class ListeningScalableLayeredPane extends ScalableLayeredPane implements
		MouseListener, MouseMotionListener {

	public ListeningScalableLayeredPane() {
		super();
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	private IFigure selectedImage;
	private int deltaX, deltaY, startX, startY;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.draw2d.MouseListener#mouseDoubleClicked(org.eclipse.draw2d
	 * .MouseEvent)
	 */
	@Override
	public void mouseDoubleClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.draw2d.MouseListener#mousePressed(org.eclipse.draw2d.MouseEvent
	 * )
	 */
	@Override
	public void mousePressed(MouseEvent arg0) {
		// sets the starting point of the movement
		startX = arg0.x;
		startY = arg0.y;

		try {

			selectedImage = findFigureAt(arg0.getLocation()); // Gets
			// ImageFigure
			// at current
			// Location
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Mouse Events should be consumed after usage to avoid ugly
		// side-effects
		arg0.consume();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.draw2d.MouseListener#mouseReleased(org.eclipse.draw2d.MouseEvent
	 * )
	 */
	@Override
	public void mouseReleased(MouseEvent arg0) {
		selectedImage = null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.draw2d.MouseMotionListener#mouseDragged(org.eclipse.draw2d
	 * .MouseEvent)
	 */
	@Override
	public void mouseDragged(MouseEvent arg0) {
		if (selectedImage != null) {
			// calculates the moving distance in x and y direction
			deltaX = arg0.x - startX;
			deltaY = arg0.y - startY;

			try {
				// are we dragging the floorplan? yes -> panning
				FloorPlanLayer fpl = (FloorPlanLayer) selectedImage.getParent();
				pan();
			} catch (ClassCastException e) {
				// not dragging the floorplan? -> just move the selected figure
				moveFigure(selectedImage);
				selectedImage.getParent().repaint();
			}
			// make sure to get the right delta
			startX = arg0.x;
			startY = arg0.y;
			arg0.consume();

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.draw2d.MouseMotionListener#mouseEntered(org.eclipse.draw2d
	 * .MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.draw2d.MouseMotionListener#mouseExited(org.eclipse.draw2d
	 * .MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.draw2d.MouseMotionListener#mouseHover(org.eclipse.draw2d.
	 * MouseEvent)
	 */
	@Override
	public void mouseHover(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.draw2d.MouseMotionListener#mouseMoved(org.eclipse.draw2d.
	 * MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * moves the handed figure - no repaint
	 * 
	 * @param figure
	 */
	private void moveFigure(IFigure figure) {
		if (figure != null) {
			Rectangle rect = figure.getBounds();
			// never forget about the scale!!
			rect.x += (deltaX / getScale());
			rect.y += (deltaY / getScale());
			figure.setBounds(rect);
		}
	}

	/**
	 * moves all the figures on all the layers + repaint
	 */
	private void pan() {
		// get all layers
		for (Object object : getChildren()) {
			// get all objects in them
			for (Object obj : ((IFigure) object).getChildren()) {
				IFigure ifig = (IFigure) obj;
				moveFigure(ifig);
			}
			((IFigure) object).repaint();
		}
	}

}
