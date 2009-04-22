/**
 * 
 */
package org.rifidi.edge.client.alelr;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.ALELRServicePortType;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.EmptyParms;
import org.rifidi.edge.client.ale.ecspecview.Activator;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class LRTreeViewPart extends ViewPart implements IPropertyChangeListener {
	/** Stub that is connected to the remote SOAP server. */
	private ALELRServicePortType lrServicePortType = null;
	/** Factory for logical reader soap connection stubs. */
	private JaxWsProxyFactoryBean lrFactory;
	/** Treeviewer. */
	private TreeViewer treeViewer;

	/**
	 * Constructor.
	 */
	public LRTreeViewPart() {
		// connect to the soap service
		lrFactory = new JaxWsProxyFactoryBean();
		lrFactory.setServiceClass(ALELRServicePortType.class);
		// get address from preferences store
		lrFactory.setAddress(Activator.getDefault().getPreferenceStore()
				.getString(Activator.ALELR_ENDPOINT));
		lrServicePortType = (ALELRServicePortType) lrFactory.create();
		// see if it works
		try {
			EmptyParms parms = new EmptyParms();
			lrServicePortType.getVendorVersion(parms);
		} catch (Throwable e) {
			lrServicePortType = null;
		}
		Activator.getDefault().getPreferenceStore().addPropertyChangeListener(
				this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse
	 * .jface.util.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(Activator.ALELR_ENDPOINT)) {
			// darn, can't reuse the factory
			lrFactory = new JaxWsProxyFactoryBean();
			lrFactory.setServiceClass(ALELRServicePortType.class);
			// use new address
			lrFactory.setAddress((String) event.getNewValue());
			lrServicePortType = (ALELRServicePortType) lrFactory.create();
			// see if it works
			try {
				EmptyParms parms = new EmptyParms();
				lrServicePortType.getVendorVersion(parms);
				treeViewer.setInput(lrServicePortType);
			} catch (Throwable e) {
				lrServicePortType = null;
			}
		}
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
		// set the model if we got a valid connection
		if (lrServicePortType != null) {
			treeViewer.setInput(lrServicePortType);
		}
		MenuManager menuMgr = new MenuManager();
		menuMgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		getSite().registerContextMenu(menuMgr, treeViewer);
		Menu menu = menuMgr.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menu);

		treeViewer.setSorter(new ViewerSorter());
		getSite().setSelectionProvider(treeViewer);
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
