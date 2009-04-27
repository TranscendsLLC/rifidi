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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	@SuppressWarnings("unused")
	private static final Log logger = LogFactory
			.getLog(SiteViewMouseWheelListener.class);

	public SiteViewMouseWheelListener(ScalableLayeredPane sp) {
		super();
		this.sp = sp;
	}

	@Override
	public void handleEvent(Event event) {
		zoom(event);
		
		
	}

	private void zoom(Event event) {
		/** if the event is caused by the wheel */
		if (event.type == SWT.MouseWheel) {
			/** if wheel is scrolled upwards and scale of the pane below 20 */
			if (event.count > 0 && sp.getScale() < 20) {
				/** scale up by 0.1 */
				sp.setScale(sp.getScale() * 1.1);
			}
			/** if scrolled downwards and scale above 0.001 */
			if (event.count < 0 // not = 0
					&& sp.getScale() > 0.001) {
				/** scale down by 0.1 */
				sp.setScale(sp.getScale() / 1.1);

			}
		}
	}

	public void centerPane(Event event){
		centerPane(event.x,event.y);
	}
	
	public void centerPane(int x, int y) {
		/** horizontal difference of the x value to the middle of the pane */
		int diffX = (sp.getBounds().width / 2) - x;
		/** vertical difference of the y value to the middle of the pane */
		int diffY = (sp.getBounds().height / 2) - y;
		/** walk through all layers of the pane */
		for (Object object : sp.getChildren()) {
			/** walk through all the figures, placed on the layers
			 *  and add the difference to center on the given x/y coordinates.
			 */
			for (Object obj : ((IFigure) object).getChildren()) {
				/** cast the resulting object to an IFigure */
				IFigure ifig = (IFigure) obj;
				/** getting the bounds of the figure */
				Rectangle bounds = ifig.getBounds();
				/** move this figure horizontally */
				bounds.x += diffX;
				/** move this figure vertically */
				bounds.y += diffY;
				/** set the changes */
				ifig.setBounds(bounds);
			}
			/** repaint the layers to see the effect of the changes */
			((IFigure) object).repaint();
		}
		// logger.debug(sp.getBounds());
		// logger.debug(event.x + " " + event.y);
	}

}
