package org.rifidi.edge.client.ale.treeview.views;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.ViewPart;
import org.rifidi.edge.client.ale.treeview.modelmanagerservice.ModelManagerService;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class AleTreeView extends ViewPart {

	public static final String ID = "org.rifidi.edge.client.ale.treeview.views.AleTreeView";

	private TreeViewer viewer;

	// private DrillDownAdapter drillDownAdapter;
	// private Action action1;
	// private Action action2;

	// private Action doubleClickAction;

	/**
	 * The constructor.
	 */
	public AleTreeView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		viewer = new TreeViewer(parent);
		viewer.setContentProvider(new AleTreeViewContentProvider());
		viewer.setLabelProvider(new AleTreeViewLabelProvider());
		viewer.setSorter(new ViewerSorter());

		getSite().setSelectionProvider(viewer);
		ModelManagerService.getInstance().addViewer(viewer);

		MenuManager menuMgr = new MenuManager();
		menuMgr.add(new GroupMarker("aletreeview"));

		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	/**
	 * @return the viewer
	 */
	public TreeViewer getViewer() {
		return viewer;
	}

}