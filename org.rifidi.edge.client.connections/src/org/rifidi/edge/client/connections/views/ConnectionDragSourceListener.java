/* 
 *  ConnectionDragSourceListener.java
 *  Created:	Aug 26, 2008
 *  Project:	RiFidi Dashboard - An RFID infrastructure monitoring tool
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.connections.views;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.rifidi.edge.client.connections.remotereader.RemoteReader;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class ConnectionDragSourceListener implements DragSourceListener {

	private TreeViewer treeViewer;
	private Log logger;

	/**
	 * 
	 */
	public ConnectionDragSourceListener(TreeViewer treeViewer) {
		this.treeViewer = treeViewer;
		logger = LogFactory.getLog(ConnectionDragSourceListener.class);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.dnd.DragSourceListener#dragFinished(org.eclipse.swt.dnd
	 * .DragSourceEvent)
	 */
	@Override
	public void dragFinished(DragSourceEvent event) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.dnd.DragSourceListener#dragSetData(org.eclipse.swt.dnd
	 * .DragSourceEvent)
	 */
	@Override
	public void dragSetData(DragSourceEvent event) {

		IStructuredSelection selection = (IStructuredSelection) treeViewer
				.getSelection();
		if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
			try {
				RemoteReader remoteReader = (RemoteReader) selection
						.getFirstElement();

				if (remoteReader != null)
					event.data = remoteReader.getID().toString();

			} catch (ClassCastException e) {
				event.detail = DND.DROP_NONE;
				event.doit = false;
				return;
			}

		}
		logger.debug("dragSetData : " + event.data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.dnd.DragSourceListener#dragStart(org.eclipse.swt.dnd.
	 * DragSourceEvent)
	 */
	@Override
	public void dragStart(DragSourceEvent event) {

		IStructuredSelection selection = (IStructuredSelection) treeViewer
				.getSelection();
		if (selection.size() > 1) // so far only one reader
			event.doit = false;
	}

}
