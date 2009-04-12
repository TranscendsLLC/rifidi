/**
 * 
 */
package org.rifidi.edge.client.sal.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.rifidi.edge.client.sal.views.CommandView;
import org.rifidi.edge.client.sal.views.EdgeServerView;
import org.rifidi.edge.client.sal.views.tags.TagView;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * 
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
		layout.addView(CommandView.ID, IPageLayout.BOTTOM, (float) .5,
				EdgeServerView.ID);
		layout.addView(IPageLayout.ID_PROP_SHEET, IPageLayout.RIGHT,
				(float) .4, CommandView.ID);
		IFolderLayout folder = layout.createFolder("left", IPageLayout.RIGHT,
				.4f, EdgeServerView.ID);
		folder.addView("org.rifidi.edge.client.twodview.views.SiteView");
		folder.addPlaceholder(TagView.ID+":*");
	}

}
