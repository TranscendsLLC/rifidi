
package org.rifidi.edge.client.sal.views;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.rifidi.edge.client.model.sal.RemoteCommandConfiguration;
import org.rifidi.edge.client.sal.controller.commands.CommandTreeContentProvider;
import org.rifidi.edge.client.sal.controller.commands.CommandTreeLabelProvider;
import org.rifidi.edge.client.sal.modelmanager.ModelManagerService;
import org.rifidi.edge.client.sal.modelmanager.ModelManagerServiceListener;

/**
 * A view to display commands currently on the server
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class CommandView extends ViewPart implements
		ITabbedPropertySheetPageContributor, ModelManagerServiceListener, DragSourceListener {

	public static final String ID = "org.rifidi.edge.client.sal.views.CommandView";
	/** The tree viewer to use */
	private AbstractTreeViewer treeViewer;

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
		treeViewer = new TreeViewer(parent);
		GridData treeViewerLayoutData = new GridData(GridData.FILL_BOTH);
		treeViewerLayoutData.horizontalSpan = 2;
		treeViewer.getControl().setLayoutData(treeViewerLayoutData);
		treeViewer.setContentProvider(new CommandTreeContentProvider());
		treeViewer.setLabelProvider(new CommandTreeLabelProvider());
		treeViewer.setComparator(new ViewerComparator());
		createContextMenu();
		treeViewer.addDragSupport(DND.DROP_MOVE, new Transfer[] { TextTransfer
				.getInstance() }, this);
		ModelManagerService.getInstance().addController(this);
		this.getSite().setSelectionProvider(treeViewer);

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		ModelManagerService.getInstance().removeController(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor#getContributorId()
	 */
	@Override
	public String getContributorId() {
		return "org.rifidi.edge.client.sal.tabbedPropContributer";
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.client.sal.modelmanager.ModelManagerServiceListener#setModel(java.lang.Object)
	 */
	@Override
	public void setModel(Object model) {
		this.treeViewer.setInput(model);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragFinished(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	@Override
	public void dragFinished(DragSourceEvent event) {
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragSetData(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	@Override
	public void dragSetData(DragSourceEvent event) {
		if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
			ISelection sel = treeViewer.getSelection();
			Object o = ((TreeSelection) sel).getFirstElement();
			event.data = ((RemoteCommandConfiguration) o).getID();
		}
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragStart(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	@Override
	public void dragStart(DragSourceEvent event) {
		ISelection sel = treeViewer.getSelection();
		Object o = ((TreeSelection) sel).getFirstElement();
		if (o instanceof RemoteCommandConfiguration) {
			event.doit = true;
			return;
		}
		event.doit = false;
		
	}

}
