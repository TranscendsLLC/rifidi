package org.rifidi.edge.client.monitoring;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * The Perspective Factory for the Event Monitor Perspective
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class PerspectiveFactory implements IPerspectiveFactory {
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
	}

}
