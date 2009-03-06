/* 
 *  LrView.java
 *  Created:	Feb 16, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.ui
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.ui.views;

import java.io.IOException;
import java.net.ServerSocket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.rifidi.edge.client.ale.ui.events.EventSink;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class LrView extends ViewPart implements IListChangeListener {

	private final static String ID = "org.rifidi.edge.client.ale.ui.views.LrView";
	private Log logger = LogFactory.getLog(LrView.class);

	private int port = 10000;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {

		parent.setLayout(new FillLayout());

				
		EventSink es = new EventSink(port);
		es.addSetChangeListener(this);
		
		

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.databinding.observable.list.IListChangeListener#handleListChange(org.eclipse.core.databinding.observable.list.ListChangeEvent)
	 */
	@Override
	public void handleListChange(ListChangeEvent arg0) {
		logger.debug("List changed: "+ arg0.getSource());
		
	}

}
