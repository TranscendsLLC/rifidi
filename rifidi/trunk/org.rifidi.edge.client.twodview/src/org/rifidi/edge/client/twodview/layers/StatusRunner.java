/* 
 *  StatusRunner.java
 *  Created:	Dec 3, 2008
 *  Project:	RiFidi Edge Client
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.twodview.layers;

import org.rifidi.edge.client.twodview.sfx.ReaderAlphaImageFigure;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class StatusRunner implements Runnable {

	public StatusRunner(ObjectLayer ol) {
		this.ol = ol;
	}

	private ObjectLayer ol;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */// TODO Auto-generated method stub
	@Override
	public void run() {

		ReaderAlphaImageFigure raif;
		/*
		 * hand over layer; for each child call status refresh
		 */
		for (Object figure : ol.getChildren()) {
			try {
				raif = (ReaderAlphaImageFigure) figure;
				raif.updateStatus();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}
