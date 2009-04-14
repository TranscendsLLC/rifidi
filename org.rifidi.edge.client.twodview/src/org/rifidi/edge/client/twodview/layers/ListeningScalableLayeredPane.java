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
package org.rifidi.edge.client.twodview.layers;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.ScalableLayeredPane;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.rifidi.edge.client.twodview.listeners.SiteViewFigureSelectionListener;
import org.rifidi.edge.client.twodview.views.SiteView;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class ListeningScalableLayeredPane extends ScalableLayeredPane implements
		MouseListener, MouseMotionListener {

	/** The figure that is currently selected */
	private IFigure selectedFigure;
	private int deltaX, deltaY, startX, startY;

	public static final int FLOORPLANLAYER = 0;
	public static final int OBJECTLAYER = 1;
	public static final int EFFECTLAYER = 2;
	public static final int NOTELAYER = 3;
	private Log logger = LogFactory.getLog(ListeningScalableLayeredPane.class);
	private Set<SiteViewFigureSelectionListener> listeners;

	public ListeningScalableLayeredPane() {
		super();
		setMaximumSize(new Dimension(1024, 768));
		addMouseListener(this);
		addMouseMotionListener(this);
		this.listeners = new HashSet<SiteViewFigureSelectionListener>();
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

			selectedFigure = findFigureAt(arg0.getLocation()); // Gets

		} catch (Exception e) {
			logger.error("ERROR: ", e);
			selectedFigure = null;

		} finally {

			for (SiteViewFigureSelectionListener l : listeners) {
				l.figureSelected(selectedFigure);
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
		// selectedFigure = null;

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
		if (selectedFigure != null) {
			// calculates the moving distance in x and y direction
			deltaX = arg0.x - startX;
			deltaY = arg0.y - startY;

			try {
				// are we dragging the floorplan? yes -> panning
				@SuppressWarnings("unused")
				FloorPlanLayer fpl = (FloorPlanLayer) selectedFigure
						.getParent();
				pan();
			} catch (ClassCastException e) {
				// not dragging the floorplan? -> just move the selected figure
				moveFigure(selectedFigure);
				selectedFigure.getParent().repaint();
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
	 * @return selectedFigure - IFigure
	 */
	public IFigure getSelectedImage() {
		return this.selectedFigure;
	}

	public void removeCurrentSelection() {
		getLayer(OBJECTLAYER).remove(selectedFigure);
	}

	public void addImageSelectionListener(
			SiteViewFigureSelectionListener listener) {
		this.listeners.add(listener);
	}

	public void remoteImageSelectionListener(
			SiteViewFigureSelectionListener listener) {
		this.listeners.remove(listener);
	}

}
