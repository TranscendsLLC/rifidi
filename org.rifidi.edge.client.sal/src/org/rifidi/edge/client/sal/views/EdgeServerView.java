/**
 * 
 */
package org.rifidi.edge.client.sal.views;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;
import org.rifidi.edge.client.model.sal.RemoteEdgeServer;

/**
 * The View for displaying readers
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class EdgeServerView extends ViewPart {

	/** The tree viewer to use */
	private AbstractTreeViewer treeViewer;
	/** The EdgeServer model for this viewer */
	private RemoteEdgeServer server;

	/**
	 * 
	 */
	public EdgeServerView() {
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

		GridLayout layout = new GridLayout(2, false);
		parent.setLayout(layout);

		server = new RemoteEdgeServer();

		treeViewer = new TreeViewer(parent);
		GridData treeViewerLayoutData = new GridData(GridData.FILL_BOTH);
		treeViewerLayoutData.horizontalSpan = 2;
		treeViewer.getControl().setLayoutData(treeViewerLayoutData);
		treeViewer.setContentProvider(new EdgeServerTreeContentProvider());
		treeViewer.setLabelProvider(new EdgeServerTreeLabelProvider());
		createContextMenu();
		treeViewer.setContentProvider(new EdgeServerTreeContentProvider());
		treeViewer.setLabelProvider(new EdgeServerTreeLabelProvider());
		treeViewer.setInput(server);
		this.getSite().setSelectionProvider(treeViewer);
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

	/**
	 * Create a context menu for this viewer
	 */
	private void createContextMenu() {
		// Create menu manager.
		MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				mgr
						.add(new GroupMarker(
								IWorkbenchActionConstants.MB_ADDITIONS));
			}
		});

		// Create menu.
		Menu menu = menuMgr.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menu);

		// Register menu for extension.
		getSite().registerContextMenu(menuMgr, treeViewer);
	}

}
