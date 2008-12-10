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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.ui.PlatformUI;
import org.rifidi.dashboard.twodview.exceptions.ReaderAlreadyInMapException;
import org.rifidi.dashboard.twodview.sfx.ReaderAlphaImageFigure;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class ObjectLayer extends XYLayer {

	// private Log logger;
	private Thread thread = null;

	/**
	 * Creates a new Layer with MouseListener and MouseMotionListener
	 */
	public ObjectLayer() {
		super();
		ServiceRegistry.getInstance().service(this);
	}

	public void addReader(ReaderAlphaImageFigure reader, Point location)
			throws ReaderAlreadyInMapException {

		if (location != null) {
			reader.setBounds(new Rectangle(location.x, location.y, reader
					.getImage().getBounds().width, reader.getImage()
					.getBounds().height));
		}
		if (this.getChildren().contains(reader)) {
			throw new ReaderAlreadyInMapException("Drag and Drop failed");

		} else {
			add(reader);
			return;
		}

	}

	public void refreshObjects() {
		ReaderAlphaImageFigure raif;
		/*
		 * hand over layer; for each child call status refresh
		 */
		for (Object figure : getChildren()) {
			try {
				raif = (ReaderAlphaImageFigure) figure;
				raif.updateStatus();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void add(IFigure figure, Object constraint, int index) {
		// TODO Auto-generated method stub
		super.add(figure, constraint, index);
		if (getChildren().size() == 1 && thread == null) {
			thread = new RefreshThread(this);
			thread.start();
			System.out.println("THREAD: ich bin da!");
		}
	}

	@Override
	public void remove(IFigure figure) {
		// TODO Auto-generated method stub
		super.remove(figure);
		if (thread != null && getChildren().size() == 0) {
			thread = null;
			System.out.println("THREAD:goodbye du schnoede welt...");
		} else
			System.out.println("WTF?");
	}

	private class RefreshThread extends Thread {

		private long refreshRate = 10000;
		private ObjectLayer layer;

		public RefreshThread(ObjectLayer layer) {
			super("ObjectLayerThread");
			this.layer = layer;
		}

		@Override
		public void run() {

			while (!isInterrupted()) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(
						new StatusRunner(layer));
				try {
					Thread.sleep(refreshRate);
				} catch (InterruptedException e) {
					interrupt();
				}
			}

		}

	}

}
