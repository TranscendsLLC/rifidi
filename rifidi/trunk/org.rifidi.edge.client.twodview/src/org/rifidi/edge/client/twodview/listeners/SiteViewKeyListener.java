
package org.rifidi.edge.client.twodview.listeners;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.rifidi.edge.client.twodview.layers.ListeningScalableLayeredPane;

/**
 * TODO: Class level comment.  
 * 
 * @author Tobias Hoppenthaler - tobias@pramari.com
 */
public class SiteViewKeyListener implements KeyListener {

	private ListeningScalableLayeredPane lp;

	/**
	 * Constructor.  
	 * 
	 * @param lp
	 */
	public SiteViewKeyListener(ListeningScalableLayeredPane lp) {
		super();
		this.lp = lp;

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		int x = 0, y = 0;

		switch (e.keyCode) {
		case SWT.ARROW_UP:
			y = 10;
			break;

		case SWT.ARROW_DOWN:
			y = -10;
			break;

		case SWT.ARROW_LEFT:
			x = 10;
			break;

		case SWT.ARROW_RIGHT:
			x = -10;
			break;
		}
		if (e.character == 'a' || e.character == 'A') {
			x = 10;
		} else if (e.character == 'd' || e.character == 'D') {
			x = -10;
		} else if (e.character == 'w' || e.character == 'W') {
			y = 10;
		} else if (e.character == 's' || e.character == 'S') {
			y = -10;
		}

		lp.translatePane(x, y);
		
		// TODO:probably obsolete
//		for (Object object : lp.getChildren()) {
//			for (Object obj : ((IFigure) object).getChildren()) {
//				IFigure ifig = (IFigure) obj;
//				Rectangle bounds = ifig.getBounds();
//				bounds.x += x;
//				bounds.y += y;
//				ifig.setBounds(bounds);
//			}
//			((IFigure)object).repaint();
//		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}