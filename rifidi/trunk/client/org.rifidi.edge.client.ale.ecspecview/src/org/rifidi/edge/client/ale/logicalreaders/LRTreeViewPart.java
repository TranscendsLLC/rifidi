
package org.rifidi.edge.client.ale.logicalreaders;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;
import org.rifidi.edge.client.ale.ecspecview.Activator;

/**
 * TODO: Class level comment.  
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public class LRTreeViewPart extends ViewPart {
	public static final String ID="org.rifidi.edge.client.ale.logicalreaders.LRView";
	/** Treeviewer. */
	private TreeViewer treeViewer;
	/** Service that manages connections to ALE. */
	private ALELRService service;

	/**
	 * Constructor.
	 */
	public LRTreeViewPart() {
		service = Activator.getDefault().getAleLrService();
	}

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
		LRTreeContentProvider provider = new LRTreeContentProvider();
		// create viewer
		treeViewer = new TreeViewer(parent, SWT.MULTI);
		treeViewer.setContentProvider(provider);
		treeViewer.setLabelProvider(new LRLabelProvider());
		MenuManager menuMgr = new MenuManager();
		menuMgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		getSite().registerContextMenu(menuMgr, treeViewer);
		Menu menu = menuMgr.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menu);
		treeViewer.setSorter(new ViewerSorter());
		getSite().setSelectionProvider(treeViewer);
		service.registerALELRViewer(treeViewer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
