/* 
 *  SiteViewDropTargetListener.java
 *  Created:	Aug 21, 2008
 *  Project:	RiFidi Dashboard - An RFID infrastructure monitoring tool
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.twodview.listeners;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.rifidi.edge.client.model.sal.RemoteEdgeServer;
import org.rifidi.edge.client.model.sal.RemoteReader;
import org.rifidi.edge.client.twodview.Activator;
import org.rifidi.edge.client.twodview.exceptions.ReaderAlreadyInMapException;
import org.rifidi.edge.client.twodview.layers.ObjectLayer;
import org.rifidi.edge.client.twodview.sfx.ReaderAlphaImageFigure;
import org.rifidi.edge.client.twodview.views.SiteView;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class SiteViewDropTargetListener implements DropTargetListener {

	private SiteView siteView;

	private Composite composite;

	private static final Log logger = LogFactory
			.getLog(SiteViewDropTargetListener.class);
	private RemoteEdgeServer server;

	/**
	 * 
	 */
	public SiteViewDropTargetListener(SiteView siteView, Composite composite) {
		super();
		this.siteView = siteView;
		this.composite = composite;
	}

	public void SetModel(RemoteEdgeServer server) {
		this.server = server;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.dnd.DropTargetListener#dragEnter(org.eclipse.swt.dnd.
	 * DropTargetEvent)
	 */
	@Override
	public void dragEnter(DropTargetEvent event) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.dnd.DropTargetListener#dragLeave(org.eclipse.swt.dnd.
	 * DropTargetEvent)
	 */
	@Override
	public void dragLeave(DropTargetEvent event) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.dnd.DropTargetListener#dragOperationChanged(org.eclipse
	 * .swt.dnd.DropTargetEvent)
	 */
	@Override
	public void dragOperationChanged(DropTargetEvent event) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.swt.dnd.DropTargetListener#dragOver(org.eclipse.swt.dnd.
	 * DropTargetEvent)
	 */
	@Override
	public void dragOver(DropTargetEvent event) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.swt.dnd.DropTargetListener#drop(org.eclipse.swt.dnd.
	 * DropTargetEvent)
	 */
	@Override
	public void drop(DropTargetEvent event) {
		logger.debug("drop");
		if (TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
			Image image = Activator.getDefault().getImageRegistry().get(
					Activator.IMG_READER);
			// get Reader from ReaderRegistry
			RemoteReader reader = (RemoteReader) server.getRemoteReaders().get(
					(String) event.data);
			if (reader == null) {
				logger.warn("No reader with ID " + event.data + " was found");
				return;
			}
			// create an ImageFigure that contains the reference to the
			ReaderAlphaImageFigure raif = new ReaderAlphaImageFigure(image,
					reader);

			ObjectLayer layer = siteView.getObjectLayer();

			Composite compost = composite;
			int deltaX = 0, deltaY = 0;

			while (compost != null) {
				// logger.debug("in loop: " + compost.toString());
				Rectangle temp = new Rectangle(compost.getBounds());
				deltaX += temp.x;
				deltaY += temp.y;
				compost = compost.getParent();
			}
			// deltaY+=20;
			// logger.debug("deltas: " + deltaX + " " + deltaY);
			raif.setBounds(new Rectangle(event.x - deltaX, event.y - deltaY,
					raif.getImage().getBounds().width, raif.getImage()
							.getBounds().height));
			try {
				layer.addReader(raif, null);
			} catch (ReaderAlreadyInMapException e) {
				// TODO : POPUP
				logger.error("ERROR: Cannot add a reader More than once ");
				MessageBox mb = new MessageBox(siteView.getSite().getShell(),
						SWT.ICON_WARNING);
				mb.setText("Reader already in map");
				mb.setMessage("Reader cannot be added to map more than once.");
				mb.open();
			}

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.dnd.DropTargetListener#dropAccept(org.eclipse.swt.dnd
	 * .DropTargetEvent)
	 */
	@Override
	public void dropAccept(DropTargetEvent event) {
	}

}
