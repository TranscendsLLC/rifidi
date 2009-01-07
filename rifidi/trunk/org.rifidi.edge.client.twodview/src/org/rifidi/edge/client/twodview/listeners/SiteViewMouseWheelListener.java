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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ScalableLayeredPane;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class SiteViewMouseWheelListener implements Listener {

	private ScalableLayeredPane sp = null;

	public SiteViewMouseWheelListener(ScalableLayeredPane sp) {
		super();
		this.sp = sp;
	}

	@Override
	public void handleEvent(Event event) {
		zoom(event);
		if (event.count > 0)
			centerPane(event);
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

	public void centerPane(Event event) {
		// TODO COMMENT
		int diffX = (sp.getBounds().width / 2) - event.x;
		int diffY = (sp.getBounds().height / 2) - event.y;
		for (Object object : sp.getChildren()) {
			for (Object obj : ((IFigure) object).getChildren()) {
				IFigure ifig = (IFigure) obj;
				Rectangle bounds = ifig.getBounds();
				bounds.x += diffX;
				bounds.y += diffY;
				ifig.setBounds(bounds);
			}
			((IFigure) object).repaint();
		}
		System.out.println(sp.getBounds());
		System.out.println(event.x + " " + event.y);
	}

}
