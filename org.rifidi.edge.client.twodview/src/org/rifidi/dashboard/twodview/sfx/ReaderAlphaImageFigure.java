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
package org.rifidi.dashboard.twodview.sfx;

import org.eclipse.swt.graphics.Image;
import org.rifidi.edge.client.connections.remotereader.RemoteReader;
import org.rifidi.edge.client.connections.remotereader.RemoteReaderID;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class ReaderAlphaImageFigure extends AlphaImageFigure {

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
	}

	public RemoteReaderID getReaderId() {
		return this.reader.getID();
	}

}
