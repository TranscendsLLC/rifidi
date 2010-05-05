/*
 * SALPerspective.java
 * 
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.client.sal.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.rifidi.edge.client.sal.views.CommandView;
import org.rifidi.edge.client.sal.views.EdgeServerView;
import org.rifidi.edge.client.sal.views.tags.TagView;

/**
 * The Perspective Factory for the SAL Perspective
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class SALPerspective implements IPerspectiveFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui
	 * .IPageLayout)
	 */
	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		layout.addView(EdgeServerView.ID, IPageLayout.LEFT, (float) .6, layout
				.getEditorArea());
		layout.getViewLayout(EdgeServerView.ID).setCloseable(false);
		layout.addView(CommandView.ID, IPageLayout.BOTTOM, (float) .5,
				EdgeServerView.ID);
		layout.getViewLayout(CommandView.ID).setCloseable(false);
		layout.addView(IPageLayout.ID_PROP_SHEET, IPageLayout.RIGHT,
				(float) .4, CommandView.ID);
		layout.getViewLayout(IPageLayout.ID_PROP_SHEET).setCloseable(false);
		IFolderLayout folder = layout.createFolder("left", IPageLayout.RIGHT,
				.4f, EdgeServerView.ID);
		//folder.addView("org.rifidi.edge.client.twodview.views.SiteView");
		folder.addPlaceholder(TagView.ID + ":*");

	}

}
