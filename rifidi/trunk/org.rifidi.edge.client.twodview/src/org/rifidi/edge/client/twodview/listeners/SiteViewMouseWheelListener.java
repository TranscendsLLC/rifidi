
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
 * TODO: Class level comment.  
 * 
 * @author Tobias Hoppenthaler - tobias@pramari.com
 */
public class SiteViewMouseWheelListener implements Listener {

	private ScalableLayeredPane sp = null;
	
	private static final Log logger = LogFactory
			.getLog(SiteViewMouseWheelListener.class);

	/**
	 * Constructor.  
	 * 
	 * @param sp
	 */
	public SiteViewMouseWheelListener(ScalableLayeredPane sp) {
		super();
		this.sp = sp;

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
	 */
	@Override
	public void handleEvent(Event event) {
		
		zoom(event);

	}

	/**
	 * This class interprets mousewheel movements that zoom in and out of the picture on screen.  
	 * 
	 * @param event
	 */
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

	/**
	 * Centers the frame of the picture on the given event.  
	 * 
	 * @param event
	 */
	public void centerPaneOnThisEvent(Event event) {
		centerPaneOnThisXY(event.x, event.y);
	}

	public void centerPaneOnThisXY(int x, int y) {
		logger.debug("Centering on " + x + ":" + y);
		/** horizontal difference of the x value to the center of the pane */
		int diffX = (sp.getBounds().width / 2) - x;
		/** vertical difference of the y value to the center of the pane */
		int diffY = (sp.getBounds().height / 2) - y;
		/** walk through all layers of the pane */
		addOffsetToPane(diffX, diffY);

	}

	/**
	 * TODO: Method level comment.  
	 * 
	 * @param xOffset
	 * @param yOffset
	 */
	private void addOffsetToPane(int xOffset, int yOffset) {
		for (Object object : sp.getChildren()) {
			/**
			 * walk through all the figures, placed on the layers and add the
			 * difference to center on the given x/y coordinates.
			 */
//			for (Object obj : ((IFigure) object).getChildren()) {
				/** cast the resulting object to an IFigure */
				IFigure ifig = (IFigure) object;
				/** getting the bounds of the figure */
				Rectangle bounds = ifig.getBounds();
				/** move this figure horizontally */
				bounds.x += xOffset;
				/** move this figure vertically */
				bounds.y += yOffset;
				/** set the changes */
				ifig.setBounds(bounds);
//			}
			/** repaint the layers to see the effect of the changes */
			((IFigure) object).repaint();
		}
	}

	

}
