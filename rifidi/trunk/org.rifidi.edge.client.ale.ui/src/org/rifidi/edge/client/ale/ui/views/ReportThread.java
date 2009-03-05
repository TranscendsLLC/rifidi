/* 
 *  ReportThread.java
 *  Created:	Mar 3, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.ui
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.ui.views;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class ReportThread extends Thread {
	private Runnable runner = null;

	/**
	 * @param target
	 */
	public ReportThread(Runnable target) {
		super(target);
		this.runner = target;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		while (!isInterrupted()) {
			runner.run();
		}
	}

}
