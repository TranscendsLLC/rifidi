/* 
 *  EventSink.java
 *  Created:	Mar 6, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.ui
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.ui.events;

import java.io.IOException;
import java.net.ServerSocket;

import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.rifidi.edge.client.ale.ui.views.ReportRunner;
import org.rifidi.edge.client.ale.ui.views.ReportThread;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class EventSink {
	private int port = -1, listeners = 0;
	private ServerSocket ss = null;
	private WritableList list = new WritableList();
	private ReportThread thread = null;

	/**
	 * 
	 */
	public EventSink(int port) {
		this.port = port;
		try {
			ss = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}

		thread = new ReportThread(new ReportRunner(ss, list));

	}

	public void addSetChangeListener(IListChangeListener listener) {
		if (listeners == 0)
			thread.start();
		list.addListChangeListener(listener);
		listeners++;
	}

	public void removeSetChangeListener(IListChangeListener listener) {
		list.removeListChangeListener(listener);
		listeners--;
		if (listeners == 0)
			thread.interrupt();
	}

}
