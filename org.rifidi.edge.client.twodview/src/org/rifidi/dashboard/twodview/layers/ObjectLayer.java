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
package org.rifidi.dashboard.twodview.layers;

import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.rifidi.dashboard.twodview.exceptions.ReaderAlreadyInMapException;
import org.rifidi.dashboard.twodview.sfx.ReaderAlphaImageFigure;
import org.rifidi.edge.client.connections.remotereader.RemoteReader;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class ObjectLayer extends XYLayer implements MouseListener,
		MouseMotionListener {

	private ReaderAlphaImageFigure selectedImage;
	private int deltaX, deltaY;

	// private Log logger;

	/**
	 * Creates a new Layer with MouseListener and MouseMotionListener
	 */
	public ObjectLayer() {
		super();
		addMouseListener(this);
		addMouseMotionListener(this);
		ServiceRegistry.getInstance().service(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.draw2d.MouseListener#mouseDoubleClicked(org.eclipse.draw2d
	 * .MouseEvent)
	 */
	@Override
	public void mouseDoubleClicked(MouseEvent me) {
		try {
			selectedImage = (ReaderAlphaImageFigure) findFigureAt(me
					.getLocation());
			if (selectedImage != null) {
				RemoteReader reader = selectedImage.getReader();
				System.out.println(reader.getID().toString());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.draw2d.MouseListener#mousePressed(org.eclipse.draw2d.MouseEvent
	 * )
	 */
	@Override
	public void mousePressed(MouseEvent me) {

		try {
			selectedImage = (ReaderAlphaImageFigure) findFigureAt(me
					.getLocation()); // Gets ImageFigure at current Location
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (selectedImage != null) {
			Rectangle rect = selectedImage.getBounds();
			deltaX = me.x - rect.x; // calculates difference of coordinates
			// between
			deltaY = me.y - rect.y;
			me.consume();
		} 

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.draw2d.MouseListener#mouseReleased(org.eclipse.draw2d.MouseEvent
	 * )
	 */
	@Override
	public void mouseReleased(MouseEvent me) {
		moveFigure(me);
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
	public void mouseDragged(MouseEvent me) {
		moveFigure(me);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.draw2d.MouseMotionListener#mouseEntered(org.eclipse.draw2d
	 * .MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent me) {
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
	public void mouseExited(MouseEvent me) {
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
	public void mouseHover(MouseEvent me) {
		

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.draw2d.MouseMotionListener#mouseMoved(org.eclipse.draw2d.
	 * MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent me) {
		// System.out.println(me.x + " - "+me.y);

	}

	/**
	 * moves the currently selected Image
	 * 
	 * @param e
	 */
	private void moveFigure(MouseEvent e) {
		if (selectedImage != null) {
			Rectangle rectangle = selectedImage.getBounds();
			selectedImage.setBounds(new Rectangle(e.x - deltaX, e.y - deltaY,
					rectangle.width, rectangle.height));
		}
	}

	
	public void addReader(ReaderAlphaImageFigure reader, Point location) throws ReaderAlreadyInMapException {
		// reader.getBounds().setLocation(location);
		// reader.getBounds().getTranslated(pt);
		if (location != null) {
			reader.setBounds(new Rectangle(location.x, location.y, reader
					.getImage().getBounds().width, reader.getImage()
					.getBounds().height));
		}
		if (this.getChildren().isEmpty() | this.getChildren().contains(reader)) {
			add(reader);
			return;
		} else throw new ReaderAlreadyInMapException("Drag and Drop failed");
		
	}

}
