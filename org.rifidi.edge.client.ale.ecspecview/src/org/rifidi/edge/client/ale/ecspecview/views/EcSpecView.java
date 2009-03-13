package org.rifidi.edge.client.ale.ecspecview.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.rifidi.edge.client.ale.api.proxy.AleProxyFactory;

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

public class EcSpecView extends ViewPart {
	private TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;

	/*
	 * The content provider class is responsible for providing objects to the
	 * view. It can wrap existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view,
	 * or ignore it and always show the same content (like Task List, for
	 * example).
	 */

//	 class TreeObject implements IAdaptable {
//	 private String name;
//	 private TreeParent parent;
//			
//	 public TreeObject(String name) {
//	 this.name = name;
//	 }
//	 public String getName() {
//	 return name;
//	 }
//	 public void setParent(TreeParent parent) {
//	 this.parent = parent;
//	 }
//	 public TreeParent getParent() {
//	 return parent;
//	 }
//	 public String toString() {
//	 return getName();
//	 }
//	 public Object getAdapter(Class key) {
//	 return null;
//	 }
//	 }
//
//	 class TreeParent extends TreeObject {
//	 private ArrayList children;
//	 public TreeParent(String name) {
//	 super(name);
//	 children = new ArrayList();
//	 }
//	 public void addChild(EdgeServerTreeObject child) {
//	 children.add(child);
//	 }
//	 public void removeChild(TreeObject child) {
//	 children.remove(child);
//	 }
//	 public TreeObject [] getChildren() {
//	 return (TreeObject [])children.toArray(new TreeObject[children.size()]);
//	 }
//	 public boolean hasChildren() {
//	 return children.size()>0;
//	 }
//	 }
//	class ViewContentProvider implements IStructuredContentProvider,
//			ITreeContentProvider {
//		private TreeParent invisibleRoot;
//
//		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
//		}
//
//		public void dispose() {
//		}
//
//		public Object[] getElements(Object parent) {
//			if (parent.equals(getViewSite())) {
//				if (invisibleRoot == null)
//					initialize();
//				return getChildren(invisibleRoot);
//			}
//			return getChildren(parent);
//		}
//
//		public Object getParent(Object child) {
//			if (child instanceof SpecTreeObject) {
//				return ((SpecTreeObject) child).getParent();
//			}
//			return null;
//		}
//
//		public Object[] getChildren(Object parent) {
//			if (parent instanceof EdgeServerTreeObject) {
//				return ((EdgeServerTreeObject) parent).getChildren();
//			}
//			return new Object[0];
//		}
//
//		public boolean hasChildren(Object parent) {
//			if (parent instanceof EdgeServerTreeObject)
//				return ((EdgeServerTreeObject) parent).hasChildren();
//			return false;
//		}
//
//		/*
//		 * We will set up a dummy model to initialize tree heararchy. In a real
//		 * code, you will connect to a real model and expose its hierarchy.
//		 */
//		private void initialize() {
//			// SpecTreeObject to1 = new SpecTreeObject("Spec 1");
//			// SpecTreeObject to2 = new SpecTreeObject("Spec 2");
//			// SpecTreeObject to3 = new SpecTreeObject("Spec 3");
//			// TreeParent p1 = new TreeParent("Edge Server 1");
//			// p1.addChild(to1);
//			// p1.addChild(to2);
//			// p1.addChild(to3);
//			//			
//			// SpecTreeObject to4 = new SpecTreeObject("Spec 4");
//			// TreeParent p2 = new TreeParent("Edge Server 2");
//			// p2.addChild(to4);
//			//			
//			// TreeParent root = new TreeParent("Root");
//			// root.addChild(p1);
//			// root.addChild(p2);
//			
//			EdgeServerTreeObject es1 = new EdgeServerTreeObject("Fosstrak");
//
//			invisibleRoot = new TreeParent("");
//			invisibleRoot.addChild(es1);
//		}
//	}

//	class ViewLabelProvider extends LabelProvider {
//
//		public String getText(Object obj) {
//			return obj.toString();
//		}
//
//		public Image getImage(Object obj) {
//			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
//			if (obj instanceof EdgeServerTreeObject)
//				imageKey = ISharedImages.IMG_OBJ_FOLDER;
//			return PlatformUI.getWorkbench().getSharedImages().getImage(
//					imageKey);
//		}
//	}

//	class NameSorter extends ViewerSorter {
//	}

	/**
	 * The constructor.
	 */
	public EcSpecView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		viewer = new TreeViewer(parent);
		drillDownAdapter = new DrillDownAdapter(viewer);
		viewer.setContentProvider(new EcSpecViewContentProvider());
		viewer.setLabelProvider(new EcSpecViewLabelProvider());
//		viewer.setSorter(new NameSorter());
		viewer.setInput(getInitialInput());
		viewer.expandAll();
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	/**
	 * @return
	 */
	private AleProxyFactory getInitialInput() {
		return new AleProxyFactory("");
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				EcSpecView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		action2 = new Action() {
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
				showMessage("Double-click detected on " + obj.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(),
				"EC Spec View", message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}