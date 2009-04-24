/**
 * 
 */
package org.rifidi.edge.client.ale.ecspecview.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.rifidi.edge.client.ale.ecspecview.views.ECSpecEditorView;
import org.rifidi.edge.client.ale.ecspecview.views.ECSpecView;

/**
 * @author kyle
 * 
 */
public class ALEPerspectiveFactory implements IPerspectiveFactory {

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
//		layout.addView(ECSpecView.ID, IPageLayout.LEFT, (float) .6, layout
//				.getEditorArea());
		
		
		IFolderLayout leftfolder = layout.createFolder("left", IPageLayout.LEFT,
				.4f, layout.getEditorArea());
		
		IFolderLayout folder = layout.createFolder("ecspecfolder", IPageLayout.RIGHT,
				.4f, layout.getEditorArea());
		folder.addPlaceholder(ECSpecEditorView.ID + ":*");
		
		leftfolder.addView(ECSpecView.ID);
		leftfolder.addView("org.rifidi.edge.client.ale.ReportReceiverView");
		
		layout.addStandaloneView("org.rifidi.edge.client.alelr.LRTreeView",
				true, IPageLayout.BOTTOM, 0.5f, ECSpecView.ID);
		
		
		layout.getViewLayout("org.rifidi.edge.client.alelr.LRTreeView")
				.setCloseable(false);
//		layout.addStandaloneView(
//				"org.rifidi.edge.client.ale.ReportReceiverView", true,
//				IPageLayout.BOTTOM, 0.5f,
//				"org.rifidi.edge.client.alelr.LRTreeView");
		layout.getViewLayout("org.rifidi.edge.client.ale.ReportReceiverView")
				.setCloseable(false);
		layout.getViewLayout(ECSpecView.ID).setCloseable(false);
	}

}
