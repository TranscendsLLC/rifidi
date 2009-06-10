package org.rifidi.edge.client.ale.ecspecview.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.rifidi.edge.client.ale.ecspecview.views.ECSpecEditorView;
import org.rifidi.edge.client.ale.ecspecview.views.ECSpecView;
import org.rifidi.edge.client.ale.logicalreaders.LRTreeViewPart;

/**
 * This class sets up the Perspective for the ALE part of the Client.
 * 
 * @author kyle
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
		// layout.addView(ECSpecView.ID, IPageLayout.LEFT, (float) .6, layout
		// .getEditorArea());

		IFolderLayout leftfolder = layout.createFolder("left",
				IPageLayout.LEFT, .4f, layout.getEditorArea());

		IFolderLayout folder = layout.createFolder("ecspecfolder",
				IPageLayout.RIGHT, .4f, layout.getEditorArea());
		folder.addPlaceholder(ECSpecEditorView.ID + ":*");

		leftfolder.addView(ECSpecView.ID);
		// leftfolder.addView(ReportReceiverViewPart.ID);

		layout.addStandaloneView(LRTreeViewPart.ID, true, IPageLayout.BOTTOM,
				0.5f, ECSpecView.ID);

		layout.getViewLayout(LRTreeViewPart.ID).setCloseable(false);

		layout.getViewLayout(ECSpecView.ID).setCloseable(false);
	}

}
