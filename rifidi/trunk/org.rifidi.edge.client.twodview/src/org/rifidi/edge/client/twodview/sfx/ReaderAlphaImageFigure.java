/* 
 *  ReaderAlphaImageFigure.java
 *  Created:	Sep 4, 2008
 *  Project:	RiFidi Dashboard - An RFID infrastructure monitoring tool
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.twodview.sfx;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.swt.graphics.Image;
import org.rifidi.edge.client.connections.remotereader.RemoteReader;
import org.rifidi.edge.client.connections.remotereader.RemoteReaderID;
import org.rifidi.edge.client.twodview.Activator;
import org.rifidi.edge.client.twodview.enums.ReaderStateEnum;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class ReaderAlphaImageFigure extends AlphaImageFigure
		implements
		org.rifidi.edge.client.connections.remotereader.listeners.ReaderStateListener {

	@Override
	public IFigure getToolTip() {
		this.setToolTip(this.createToolTip());
		return super.getToolTip();
	}

	RemoteReader reader;

	/**
	 * @return the reader
	 */
	public RemoteReader getReader() {
		return reader;
	}

	/**
	 * @param reader
	 *            the reader to set
	 */
	public void setReader(RemoteReader reader) {
		this.reader = reader;
	}

	/**
	 * @param image
	 * @param reader
	 */
	public ReaderAlphaImageFigure(Image image, RemoteReader reader) {
		super(image);
		this.reader = reader;
		this.reader.addStateListener(this);
		this.setToolTip(this.createToolTip());
	}

	public RemoteReaderID getReaderId() {
		return this.reader.getID();
	}

	private IFigure createToolTip() {
		Label ttl = null;
		if (reader == null) {
			ttl = new Label("I AM JUST A DUMMY\nsorry, no status here");
		} else {
			ttl = new Label(reader.getDescription() + "\n"
					+ reader.getReaderState());
		}
		// updateStatus();
		return ttl;
	}

	public void updateStatus(String state) {
		boolean handled = false;

		if (state.equals(ReaderStateEnum.CONFIGURED.toString())) {
			this.setImage(Activator
					.imageDescriptorFromPlugin(
							"org.rifidi.edge.client.twodview",
							"icons/reader-24x24.png").createImage());
			handled = true;
		}
		if (state.equals(ReaderStateEnum.OK.toString())) {
			this.setImage(Activator.imageDescriptorFromPlugin(
					"org.rifidi.edge.client.twodview",
					"icons/reader-24x24_on.png").createImage());
			handled = true;
		}
		if (state.equals(ReaderStateEnum.ERROR.toString())) {
			this.setImage(Activator.imageDescriptorFromPlugin(
					"org.rifidi.edge.client.twodview",
					"icons/reader-24x24_off.png").createImage());
			handled = true;
		}
		if (state.equals(ReaderStateEnum.EXECUTING_COMMAND.toString())) {
			this.setImage(Activator.imageDescriptorFromPlugin(
					"org.rifidi.edge.client.twodview",
					"icons/reader-24x24_err.png").createImage());
			handled = true;
		}
		if (!handled) {
			// set an image with question mark
		}
	}

	public void updateStatus() {
		/*
		 * check status update picture
		 */
		String state = reader.getReaderState();
		updateStatus(state);

	}

	@Override
	public void readerStateChanged(RemoteReaderID readerID, String newState) {
		updateStatus(newState);

	}
}
