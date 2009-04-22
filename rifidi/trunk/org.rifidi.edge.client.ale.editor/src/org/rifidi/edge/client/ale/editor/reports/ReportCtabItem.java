/* 
 *  ReportCtabItem.java
 *  Created:	Apr 22, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.editor
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.editor.reports;

import java.io.IOException;
import java.net.ServerSocket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class ReportCtabItem extends CTabItem {
	private Text text = null;
	private ReportThread thread = null;
	private Log logger = LogFactory.getLog(ReportCtabItem.class);

	/**
	 * @see org.eclipse.swt.custom.CTabItem
	 */
	public ReportCtabItem(CTabFolder parent, int style) {
		super(parent, style);
		parent.setLayout(new FillLayout());
		// The text widget to display the tags
		text = new Text(parent, SWT.MULTI | SWT.READ_ONLY);
		this.setControl(text);

		//create the server socket for the subscriber URI
		ServerSocket ss;
		try {
			ss = new ServerSocket(10001);
			// thread that refreshes the text and the tab's caption
			thread = new ReportThread(new ReportRunner(ss, text, this));
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		// starting on creation now for testing, on selection did not work
		thread.start();

//		addListener(SWT.Selection, new Listener() {
//
//			@Override
//			public void handleEvent(Event event) {
//				thread.start();
//
//			}
//
//		});
		//attempt to  stop the thread on focus loss
		addListener(SWT.FocusOut, new Listener() {

			@Override
			public void handleEvent(Event event) {
				thread.interrupt();

			}

		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.custom.CTabItem#dispose()
	 */
	@Override
	public void dispose() {
		thread.interrupt();
		super.dispose();
	}

	/**
	 * 
	 * Refresh thread.
	 *
	 */
	private class ReportThread extends Thread {

		private Runnable target = null;

		/**
		 * @param target
		 */
		public ReportThread(Runnable target) {
			super();
			this.target = target;
			// TODO Auto-generated constructor stub
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			while (!isInterrupted())
				// injecting the worker runnable in the eclipse thread
				getParent().getDisplay().syncExec(target);
				try {
					// tried to make it less heavyweight by just running every .5 sec
					Thread.sleep(500);
				} catch (InterruptedException e) {
					logger.error(e.getMessage());
				}

		}

	}

}
