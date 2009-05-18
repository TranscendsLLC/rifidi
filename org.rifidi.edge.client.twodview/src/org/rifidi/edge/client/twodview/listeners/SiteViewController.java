/* 
 *  SiteViewController.java
 *  Created:	Aug 21, 2008
 *  Project:	RiFidi Dashboard - An RFID infrastructure monitoring tool
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.twodview.listeners;

//TODO: Comments
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.map.IMapChangeListener;
import org.eclipse.core.databinding.observable.map.MapChangeEvent;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.rifidi.edge.client.model.sal.RemoteEdgeServer;
import org.rifidi.edge.client.model.sal.RemoteEdgeServerState;
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
public class SiteViewController implements DropTargetListener,
		IMapChangeListener, PropertyChangeListener {

	private SiteView siteView;

	private Composite composite;

	private HashMap<String, ReaderAlphaImageFigure> readerIDsToReaderFigures;

	private static final Log logger = LogFactory
			.getLog(SiteViewController.class);
	private RemoteEdgeServer server;

	/**
	 * Constructor.
	 * 
	 * @param siteView
	 * @param composite
	 */
	public SiteViewController(SiteView siteView, Composite composite) {
		super();
		this.siteView = siteView;
		this.composite = composite;
		this.readerIDsToReaderFigures = new HashMap<String, ReaderAlphaImageFigure>();
	}

	public void SetModel(RemoteEdgeServer server) {
		if (this.server != null) {
			this.server.removePropertyChangeListener(this);
			this.server.removePropertyChangeListener(this);
		}
		this.server = server;
		this.server.getRemoteReaders().addMapChangeListener(this);
		this.server.addPropertyChangeListener(this);
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
		if (TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
			addReaderToMap((String) event.data, event.x, event.y, true);
		}

	}

	/**
	 * Adds an image representation of the reader identified by readerID to the
	 * map and positions it at readerPosition x/y-coordinates.
	 * 
	 * @param readerID
	 * @param readerPositionX
	 * @param readerPositionY
	 * @param dnd
	 *            - defines whether the method is called by a Drop event (true)
	 *            or not.
	 */
	public void addReaderToMap(String readerID, int readerPositionX,
			int readerPositionY, boolean dnd) {

		if (checkReaderAlreadyOnMap(readerID)) {
			if (dnd) {
				logger.error("ERROR: Cannot add a reader More than once ");
				MessageBox mb = new MessageBox(siteView.getSite().getShell(),
						SWT.ICON_WARNING);
				mb.setText("Reader already in map");
				mb.setMessage("Reader cannot be added to map more than once.");
				mb.open();
				return;
			} else {

				ReaderAlphaImageFigure fig = this.readerIDsToReaderFigures
						.get(readerID);
				Rectangle rect = fig.getBounds();
				rect.x = readerPositionX;
				rect.y = readerPositionY;
				fig.setBounds(rect);
				return;
			}

		}
		Image image = Activator.getDefault().getImageRegistry().get(
				Activator.IMG_READER_UNKNOWN);
		// get Reader from ReaderRegistry
		RemoteReader reader = (RemoteReader) server.getRemoteReaders().get(
				readerID);

		// create an ImageFigure that contains the reference to the
		ReaderAlphaImageFigure raif = new ReaderAlphaImageFigure(image,
				readerID);
		if (reader != null) {
			raif.setReader(reader);
		}

		this.readerIDsToReaderFigures.put(raif.getReaderId(), raif);

		ObjectLayer layer = siteView.getObjectLayer();

		Composite compost = composite;
		int deltaX = 0, deltaY = 0;

		if (dnd) {
			while (compost != null) {
				// logger.debug("in loop: " + compost.toString());
				Rectangle temp = new Rectangle(compost.getBounds());
				deltaX += temp.x;
				deltaY += temp.y;
				compost = compost.getParent();
			}
		}
		// deltaY+=20;
		// logger.debug("deltas: " + deltaX + " " + deltaY);
		raif.setBounds(new Rectangle(readerPositionX - deltaX, readerPositionY
				- deltaY, raif.getImage().getBounds().width, raif.getImage()
				.getBounds().height));
		try {
			layer.addReader(raif, null);
		} catch (ReaderAlreadyInMapException e) {

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

	@Override
	public void handleMapChange(MapChangeEvent event) {
		for (Object key : event.diff.getChangedKeys()) {
			Object newVal = event.diff.getNewValue(key);
			Object oldVal = event.diff.getOldValue(key);
			if ((newVal instanceof RemoteReader)
					&& (oldVal instanceof RemoteReader)) {
				if ((newVal != null) && (oldVal != null)) {
					RemoteReader oldReader = (RemoteReader) oldVal;
					RemoteReader newReader = (RemoteReader) newVal;
					if (this.readerIDsToReaderFigures.containsKey(oldReader
							.getID())) {
						this.readerIDsToReaderFigures.get(oldReader.getID())
								.setReader(newReader);
					}
				}
			}
		}

		for (Object key : event.diff.getRemovedKeys()) {
			Object oldVal = event.diff.getOldValue(key);
			if (oldVal != null && oldVal instanceof RemoteReader) {
				RemoteReader oldReader = (RemoteReader) oldVal;
				if (this.readerIDsToReaderFigures
						.containsKey(oldReader.getID())) {
					this.readerIDsToReaderFigures.get(oldReader.getID())
							.setReader(null);
				}
			}
		}

		for (Object key : event.diff.getAddedKeys()) {
			Object newVal = event.diff.getNewValue(key);
			if ((newVal != null) && (newVal instanceof RemoteReader)) {
				RemoteReader newReader = (RemoteReader) newVal;
				if (this.readerIDsToReaderFigures
						.containsKey(newReader.getID())) {
					this.readerIDsToReaderFigures.get(newReader.getID())
							.setReader(newReader);
				}
			}
		}

	}

	public void deleteReader(String readerID) {
		siteView.getLayeredPane().removeCurrentSelection();
		this.readerIDsToReaderFigures.remove(readerID).dispose();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(RemoteEdgeServer.STATE_PROPERTY)) {
			if (this.server.getState() == RemoteEdgeServerState.CONNECTED) {
				for (ReaderAlphaImageFigure r : this.readerIDsToReaderFigures
						.values()) {
					Object o = this.server.getRemoteReaders().get(
							r.getReaderId());
					if (o != null) {
						r.setReader((RemoteReader) o);
					}
				}
			}
		}

	}

	public boolean checkReaderAlreadyOnMap(String readerID) {
		return readerIDsToReaderFigures.containsKey(readerID);
	}
}
