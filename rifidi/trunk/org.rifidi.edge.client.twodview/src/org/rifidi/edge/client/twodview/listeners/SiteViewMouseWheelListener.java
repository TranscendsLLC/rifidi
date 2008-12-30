/* 
 *  SiteViewMouseWheelListener.java
 *  Created:	Nov 7, 2008
 *  Project:	RiFidi Dashboard - An RFID infrastructure monitoring tool
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.twodview.listeners;

import org.eclipse.draw2d.ScalableLayeredPane;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.rifidi.edge.client.twodview.views.SiteView;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class SiteViewMouseWheelListener implements Listener {

	private SiteView siteView = null;
	private ScalableLayeredPane sp = null;

	public SiteViewMouseWheelListener(SiteView siteView) {
		super();
		this.siteView = siteView;
		this.sp = siteView.getLayeredPane();
	}

	@Override
	public void handleEvent(Event event) {

		// centerPane(event);
		zoom(event);

	}

	public void zoom(Event event) {
		// WHEEL
		if (event.type == SWT.MouseWheel) {
			if (event.count > 0 && sp.getScale() < 20) {
				sp.setScale(sp.getScale() * 1.1);
			}
			if (event.count < 0 // not = 0
					&& sp.getScale() > 0.001) {
				sp.setScale(sp.getScale() / 1.1);

			}
		}
	}

	public void center(Event event) {
		System.out.println(siteView.getViewSite().getWorkbenchWindow()
				.getWorkbench().getDisplay().getBounds());
	}

}
