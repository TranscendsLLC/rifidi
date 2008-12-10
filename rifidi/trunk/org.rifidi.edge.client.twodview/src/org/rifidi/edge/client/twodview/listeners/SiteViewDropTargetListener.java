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
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.rifidi.edge.client.connections.registryservice.RemoteReaderConnectionRegistryService;
import org.rifidi.edge.client.connections.remotereader.RemoteReader;
import org.rifidi.edge.client.connections.remotereader.RemoteReaderID;
import org.rifidi.edge.client.twodview.Activator;
import org.rifidi.edge.client.twodview.exceptions.ReaderAlreadyInMapException;
import org.rifidi.edge.client.twodview.layers.ObjectLayer;
import org.rifidi.edge.client.twodview.sfx.ReaderAlphaImageFigure;
import org.rifidi.edge.client.twodview.views.SiteView;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class SiteViewDropTargetListener implements DropTargetListener {

	private SiteView siteView;

	private Composite composite;

	private Log logger;

	private RemoteReaderConnectionRegistryService readerRegistry;

	/**
	 * 
	 */
	public SiteViewDropTargetListener(SiteView siteView, Composite composite) {
		super();
		this.siteView = siteView;
		this.composite = composite;
		logger = LogFactory.getLog(SiteViewDropTargetListener.class);
		ServiceRegistry.getInstance().service(this);

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
		// logger.debug("in Method dragEnter");

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
		// logger.debug("in Method dragLeave");

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
		// logger.debug("in Method dragOperationChanged");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.swt.dnd.DropTargetListener#dragOver(org.eclipse.swt.dnd.
	 * DropTargetEvent)
	 */
	@Override
	public void dragOver(DropTargetEvent event) {
		// logger.debug("in Method dragOver");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.swt.dnd.DropTargetListener#drop(org.eclipse.swt.dnd.
	 * DropTargetEvent)
	 */
	@Override
	public void drop(DropTargetEvent event) {

		if (TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
			Image image = Activator.imageDescriptorFromPlugin("org.rifidi.edge.client.twodview", "icons/reader-24x24.png").createImage();
			// get Reader from ReaderRegistry
			RemoteReader reader = readerRegistry.getRemoteReader(RemoteReaderID.createIDFromString(event.data.toString()));
			// create an ImageFigure that contains the reference to the reader
			ReaderAlphaImageFigure raif = new ReaderAlphaImageFigure(image,
					reader);
			

			ObjectLayer layer = siteView.getObjectLayer();

			Composite compost = composite;
			int deltaX = 0, deltaY = 0;

			while (compost != null) {
//				logger.debug("in loop: " + compost.toString());
				Rectangle temp = new Rectangle(compost.getBounds());
				deltaX += temp.x;
				deltaY += temp.y;
				compost = compost.getParent();
			}
//			 deltaY+=20;
			logger.debug("deltas: " + deltaX + " " + deltaY);
			raif.setBounds(new Rectangle(event.x - deltaX, event.y - deltaY,
					raif.getImage().getBounds().width, raif.getImage()
							.getBounds().height));
			try {
				layer.addReader(raif,null);
			} catch (ReaderAlreadyInMapException e) {
				// TODO : POPUP
				e.printStackTrace(); 
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
		// TODO Auto-generated method stub

	}

	@Inject
	public void setRemoteReaderRegistry(
			RemoteReaderConnectionRegistryService remoteReaderConnectionRegistryService) {
		this.readerRegistry = remoteReaderConnectionRegistryService;

	}

}
