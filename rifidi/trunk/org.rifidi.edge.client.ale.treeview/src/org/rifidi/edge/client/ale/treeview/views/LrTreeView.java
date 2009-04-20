/* 
 *  LrTreeView.java
 *  Created:	Apr 20, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.treeview
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.treeview.views;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.ViewPart;
import org.rifidi.edge.client.ale.treeview.modelmanagerservice.LrModelManagerService;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class LrTreeView extends ViewPart {

	public static final String ID = "org.rifidi.edge.client.ale.treeview.views.LrTreeView";
	private TreeViewer viewer;

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
		viewer=new TreeViewer(parent);
		viewer.setContentProvider(new LrTreeViewContentProvider());
		viewer.setLabelProvider(new LrTreeViewLabelProvider());
		viewer.setSorter(new ViewerSorter());
		getSite().setSelectionProvider(viewer);
		LrModelManagerService.getInstance().addViewer(viewer);
		//add Button(s) for wizard
		MenuManager menuMgr = new MenuManager();
		menuMgr.add(new GroupMarker("lrtableview"));
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();

	}

}
