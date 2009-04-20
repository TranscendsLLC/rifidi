package org.rifidi.edge.client.ale.treeview.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.ViewPart;
import org.rifidi.edge.client.ale.models.aleserviceporttype.AleServicePortTypeWrapper;
import org.rifidi.edge.client.ale.models.serviceprovider.SpecDataManager;
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
//	private DrillDownAdapter drillDownAdapter;
//	private Action action1;
//	private Action action2;

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
		// drillDownAdapter = new DrillDownAdapter(viewer);
		viewer.setContentProvider(new AleTreeViewContentProvider());
		viewer.setLabelProvider(new AleTreeViewLabelProvider());
		viewer.setSorter(new ViewerSorter());
		// viewer.setInput(createInitialInput());
		getSite().setSelectionProvider(viewer);
		ModelManagerService.getInstance().addViewer(viewer);
		// viewer.expandAll();
		// makeActions();
		// hookContextMenu();
		// hookDoubleClickAction();
		// contributeToActionBars();
		MenuManager menuMgr = new MenuManager();
		menuMgr.add(new GroupMarker("aletreeview"));

		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);

	}

	/**
	 * @return
	 */
	private List createInitialInput() {
		ArrayList<SpecDataManager> input = new ArrayList<SpecDataManager>();
		input.add(new AleServicePortTypeWrapper());
		return input;
	}

	// private void hookContextMenu() {
	// MenuManager menuMgr = new MenuManager("#PopupMenu");
	// menuMgr.setRemoveAllWhenShown(true);
	// menuMgr.addMenuListener(new IMenuListener() {
	// public void menuAboutToShow(IMenuManager manager) {
	// AleTreeView.this.fillContextMenu(manager);
	// }
	// });
	// Menu menu = menuMgr.createContextMenu(viewer.getControl());
	// viewer.getControl().setMenu(menu);
	// getSite().registerContextMenu(menuMgr, viewer);
	// }

	// private void contributeToActionBars() {
	// IActionBars bars = getViewSite().getActionBars();
	// fillLocalPullDown(bars.getMenuManager());
	// fillLocalToolBar(bars.getToolBarManager());
	// }

	// private void fillLocalPullDown(IMenuManager manager) {
	// manager.add(action1);
	// manager.add(new Separator());
	// manager.add(action2);
	// }

	// private void fillContextMenu(IMenuManager manager) {
	// manager.add(action1);
	// manager.add(action2);
	// manager.add(new Separator());
	// drillDownAdapter.addNavigationActions(manager);
	// // Other plug-ins can contribute there actions here
	// manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	// }

	// private void fillLocalToolBar(IToolBarManager manager) {
	// manager.add(action1);
	// manager.add(action2);
	// manager.add(new Separator());
	// drillDownAdapter.addNavigationActions(manager);
	// }

	// private void makeActions() {
	// action1 = new Action() {
	// public void run() {
	// // showMessage("Action 1 executed");
	// viewer.refresh();
	// // viewer.expandAll();
	// }
	// };
	// action1.setText("Refresh");
	// action1.setToolTipText("Refresh");
	// action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
	// .getImageDescriptor(ISharedImages.IMG_ETOOL_HOME_NAV));
	//
	// action2 = new Action() {
	// public void run() {
	//
	// ISelection selection = viewer.getSelection();
	// Object obj = ((IStructuredSelection) selection)
	// .getFirstElement();
	// if (obj instanceof String) {
	// showMessage(obj.toString());
	// }
	//
	// // showMessage("Action 2 executed");
	// }
	// };
	// action2.setText("Action 2");
	// action2.setToolTipText("Action 2 tooltip");
	// action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
	// .getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
	// doubleClickAction = new Action() {
	// public void run() {
	// ISelection selection = viewer.getSelection();
	// Object obj = ((IStructuredSelection) selection)
	// .getFirstElement();
	// showMessage("Double-click detected on " + obj.toString());
	// }
	// };
	// }

	// private void hookDoubleClickAction() {
	// viewer.addDoubleClickListener(new IDoubleClickListener() {
	// public void doubleClick(DoubleClickEvent event) {
	// doubleClickAction.run();
	// }
	// });
	// }

	// private void showMessage(String message) {
	// MessageDialog.openInformation(viewer.getControl().getShell(),
	// "EC Spec View", message);
	// }

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