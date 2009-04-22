/**
 * 
 */
package org.rifidi.edge.client.ale.ecspecview.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.rifidi.edge.client.ale.ecspecview.views.ALEEditorView;
import org.rifidi.edge.client.ale.ecspecview.views.ECSpecView;

/**
 * @author kyle
 *
 */
public class ALEPerspectiveFactory implements IPerspectiveFactory {

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		layout.addView(ECSpecView.ID, IPageLayout.LEFT, (float) .6, layout
				.getEditorArea());
		layout.getViewLayout(ECSpecView.ID).setCloseable(false);
		IFolderLayout folder = layout.createFolder("left", IPageLayout.RIGHT,
				.4f, ECSpecView.ID);
		folder.addPlaceholder(ALEEditorView.ID+":*");

	}

}
