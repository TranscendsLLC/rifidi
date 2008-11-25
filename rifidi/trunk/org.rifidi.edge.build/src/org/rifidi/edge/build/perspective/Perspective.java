package org.rifidi.edge.build.perspective;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.rifidi.edge.build.views.EdgeServerView;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {

		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);

		// layout.addStandaloneView(MainView.ID, false, IPageLayout.TOP, 1.0f,
		// editorArea);
		layout.addStandaloneView(EdgeServerView.ID, false, IPageLayout.TOP,
				1.0f, editorArea);
	}
}
