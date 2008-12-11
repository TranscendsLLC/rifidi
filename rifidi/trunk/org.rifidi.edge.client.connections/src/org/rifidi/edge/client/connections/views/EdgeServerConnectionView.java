package org.rifidi.edge.client.connections.views;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.rifidi.edge.client.connections.edgeserver.EdgeServerConnection;
import org.rifidi.edge.client.connections.handlers.RemoveReaderHandler;
import org.rifidi.edge.client.connections.registryservice.EdgeServerConnectionRegistryService;
import org.rifidi.edge.client.connections.remotereader.RemoteReader;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

public class EdgeServerConnectionView extends ViewPart implements
		ITabbedPropertySheetPageContributor {

	public static final String ID = "org.rifidi.edge.client.readerview.views.EdgeServerConnectionView";

	// private Log logger = LogFactory.getLog(EdgeServerConnectionView.class);

	private EdgeServerConnectionRegistryService serverRegistry;
	private TreeViewer treeViewer;

	private RefreshThread refreshThread;

	private Spinner autoUpdateIntervalSpinner;

	// private boolean isDragging;
	// private String[] processedDropFiles;

	public EdgeServerConnectionView() {
		ServiceRegistry.getInstance().service(this);
	}

	@Override
	public void createPartControl(Composite parent) {

		GridLayout layout = new GridLayout(2, false);
		parent.setLayout(layout);

		Button autoUpdateButton = new Button(parent, SWT.CHECK);
		autoUpdateButton.setText("enable auto refresh (sec)");
		autoUpdateButton.setEnabled(true);

		autoUpdateIntervalSpinner = new Spinner(parent, SWT.NONE);
		autoUpdateIntervalSpinner.setSelection(1);

		treeViewer = new TreeViewer(parent);
		GridData treeViewerLayoutData = new GridData(GridData.FILL_BOTH);
		treeViewerLayoutData.horizontalSpan = 2;
		treeViewer.getControl().setLayoutData(treeViewerLayoutData);
		treeViewer.setContentProvider(new ReaderViewContentProvider());
		treeViewer.setLabelProvider(new ReaderViewLabelProvider());

		treeViewer.setInput(serverRegistry);

		createContextMenu();

		treeViewer.getControl().addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.DEL) {
					Object element = ((IStructuredSelection) treeViewer
							.getSelection()).getFirstElement();
					if (element instanceof RemoteReader
							|| element instanceof EdgeServerConnection) {
						IHandlerService handlerService = (IHandlerService) getSite()
								.getService(IHandlerService.class);
						try {
							handlerService.executeCommand(
									RemoveReaderHandler.ID, null);
						} catch (ExecutionException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NotDefinedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NotEnabledException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NotHandledException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}

			}
		});

		autoUpdateButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {

				Button b = (Button) e.getSource();
				if (b.getSelection()) {
					if (refreshThread == null) {
						refreshThread = new RefreshThread();
						refreshThread.updateInterval(autoUpdateIntervalSpinner
								.getSelection());
						refreshThread.start();
					}
				} else {
					if (refreshThread != null)
						refreshThread.interrupt();
				}

			}
		});

		autoUpdateIntervalSpinner.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (refreshThread != null) {
					refreshThread.updateInterval(((Spinner) e.getSource())
							.getSelection());
				}
			}
		});

		this.getSite().setSelectionProvider(treeViewer);

		// adding DND capability to the treeviewer
		int operations = DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK
				| DND.DROP_DEFAULT;
		Transfer[] transferTypes = new Transfer[] { TextTransfer.getInstance() };

		treeViewer.addDragSupport(operations, transferTypes,
				new ConnectionDragSourceListener(treeViewer));

	}

	/**
	 * Creates the DND DragSource for Readers being dragged to a view/editor
	 * 
	 * @param tree
	 * @return
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

	@Override
	public void setFocus() {
		treeViewer.getControl().setFocus();
	}

	public void refresh() {
		treeViewer.refresh();
		ISelection sel = treeViewer.getSelection();
		treeViewer.setSelection(null);
		treeViewer.setSelection(sel);
	}

	public void dispose() {
		if (refreshThread != null) {
			refreshThread.interrupt();
		}
		super.dispose();
	}

	private class RefreshThread extends Thread {

		private int updateInterval = 1;
		
		public RefreshThread() {
			super(getViewSite().getSecondaryId().replace("&colon;", ":") + " Refresh thread.");
			// TODO Auto-generated constructor stub
		}

		@Override
		public void run() {
			try {
				while (!isInterrupted()) {
					// logger.debug("Updating");
					treeViewer.getTree().getDisplay().syncExec(new Runnable() {
						@Override
						public void run() {
							if (!treeViewer.getControl().isDisposed()) {
								treeViewer.refresh();

							} else {
								interrupt();
							}
						}
					});
					Thread.sleep(updateInterval * 1000);
				}
			} catch (InterruptedException e) {
			}
		}

		public void updateInterval(int sec) {
			updateInterval = sec;
		}
	}

	@Inject
	public void setEdgeServerConnectionRegistryService(
			EdgeServerConnectionRegistryService edgeServerConnectionRegistryService) {
		this.serverRegistry = edgeServerConnectionRegistryService;
	}

	@Override
	public String getContributorId() {
		return getSite().getId();
	}

	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == IPropertySheetPage.class) {
			IAdapterManager am = Platform.getAdapterManager();
			int status = am.queryAdapter(this, IPropertySheetPage.class
					.getName());
			if (status == IAdapterManager.NOT_LOADED) {
				am.loadAdapter(this, IPropertySheetPage.class.getName());
			}
		}
		return super.getAdapter(adapter);
	}

}
