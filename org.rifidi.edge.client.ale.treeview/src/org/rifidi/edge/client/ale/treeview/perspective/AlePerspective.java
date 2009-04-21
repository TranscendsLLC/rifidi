/* 
 *  AlePerspective.java
 *  Created:	Mar 12, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.treeview
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.treeview.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.rifidi.edge.client.ale.treeview.views.AleTreeView;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class AlePerspective implements IPerspectiveFactory {

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
		IFolderLayout folder = layout.createFolder("left", IPageLayout.RIGHT,
				.4f, AleTreeView.ID);
		folder
				.addPlaceholder("org.rifidi.edge.client.ale.editor.view.aleeditorview"
						+ ":*");
	}

}
