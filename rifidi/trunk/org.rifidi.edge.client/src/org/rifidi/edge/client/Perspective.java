/*
 * Perspective.java
 * 
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.client;

import org.eclipse.ui.IPageLayout;

import org.eclipse.ui.IPerspectiveFactory;

/**
 * TODO: Class level comment.  
 * 
 * @author jochen
 */
public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		// IFolderLayout leftFolder = layout.createFolder("leftFolder",
		// IPageLayout.LEFT, 0.3f, layout.getEditorArea());
		// leftFolder.addView("org.rifidi.edge.client.readerview.views.EdgeServerConnectionView");
		// layout.addStandaloneView("org.rifidi.edge.client.twodview.views.SiteView",
		// false, IPageLayout.RIGHT, 0f,
		// layout.getEditorArea());
		// IFolderLayout bottomFolder = layout.createFolder("bottomFolder",
		// IPageLayout.BOTTOM, 0.6f,
		// "org.rifidi.edge.client.twodview.views.SiteView");
		// bottomFolder.addView("org.rifidi.edge.client.properties.view.MyPropertySheet");
		// bottomFolder.addView("org.rifidi.edge.client.commands.views.ReaderCommandView");

		// layout.addPlaceholder("left", IPageLayout.LEFT, 0.3f, layout
		// .getEditorArea());
		// layout.addPlaceholder("display", IPageLayout.RIGHT, 0.7f, layout
		// .getEditorArea());
		// IPlaceholderFolderLayout bottomFolder =
		// layout.createPlaceholderFolder(
		// "bottomFolder", IPageLayout.BOTTOM, 0.6f, "display");
		// bottomFolder.addPlaceholder("bottomFolderPlaceholder");

	}
}
