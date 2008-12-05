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
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.ui.PlatformUI;
import org.rifidi.dashboard.twodview.views.SiteView;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class ListeningScalableLayeredPane extends ScalableLayeredPane implements
		MouseListener, MouseMotionListener {

	private IFigure selectedImage;
	private SiteView siteView;
	private int deltaX, deltaY, startX, startY;
	public static final int FLOORPLANLAYER = 0;
	public static final int OBJECTLAYER = 1;
	public static final int EFFECTLAYER = 2;
	public static final int NOTELAYER = 3;
	private StatusRunner thread;

	public ListeningScalableLayeredPane() {
		super();
		setMaximumSize(new Dimension(1024, 768));
		addMouseListener(this);
		addMouseMotionListener(this);
		thread = null;

	}

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
		selectedImage = null;
		// sets the starting point of the movement
		startX = arg0.x;
		startY = arg0.y;

		try {
			siteView = (SiteView) PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage().findView(
							"org.rifidi.dashboard.twodview.views.SiteView");

			selectedImage = findFigureAt(arg0.getLocation()); // Gets
			// ImageFigure
			// at current
			// Location

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getCause());
			e.printStackTrace();
			selectedImage = null;

		} finally {
			if (siteView != null) {
				System.out.println("strucselec: " + siteView.getSelection());
				siteView.fireSelectionChanged();
			}
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
			rect.x += deltaX;
			rect.y += deltaY;
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

	/**
	 * Returns the currently selected IFigure element on the pane can be in each
	 * one of the layers
	 * 
	 * @return selectedImage - IFigure
	 */
	public IFigure getSelectedImage() {
		try {
			if (selectedImage != null) {
				System.out.println("getselim" + selectedImage.toString());
				return this.selectedImage;
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Sets the selected Image in the pane.
	 * 
	 * @param selectedImage
	 */
	public void setSelectedImage(IFigure selectedImage) {
		this.selectedImage = selectedImage;
	}

	public void removeCurrentSelection() {
		// if(selectedImage!=null){
		getLayer(OBJECTLAYER).remove(selectedImage);
		// selectedImage=null;
		// }

	}

}
