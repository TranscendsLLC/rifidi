/* 
 *  ReaderStateListener.java
 *  Created:	10.12.2008
 *  Project:	org.rifidi.edge.client.twodview
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.twodview.sfx;

import org.rifidi.edge.client.connections.remotereader.RemoteReaderID;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 *
 */
public class ReaderStateListener
		implements
		org.rifidi.edge.client.connections.remotereader.listeners.ReaderStateListener {

	private ReaderAlphaImageFigure raif;
	/* (non-Javadoc)
	 * @see org.rifidi.edge.client.connections.remotereader.listeners.ReaderStateListener#readerStateChanged(org.rifidi.edge.client.connections.remotereader.RemoteReaderID, java.lang.String)
	 */
	@Override
	public void readerStateChanged(RemoteReaderID readerID, String newState) {
		raif.updateStatus(newState);

	}

	public ReaderStateListener(ReaderAlphaImageFigure raif) {
		super();
		this.raif=raif;
	}

}
