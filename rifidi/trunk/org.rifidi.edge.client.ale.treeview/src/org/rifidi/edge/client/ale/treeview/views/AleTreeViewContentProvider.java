/* 
 *  AleTreeViewContentProvider.java
 *  Created:	Mar 12, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.treeview
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.treeview.views;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.set.ISetChangeListener;
import org.eclipse.core.databinding.observable.set.SetChangeEvent;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.Viewer;
import org.rifidi.edge.client.ale.models.alelrserviceporttype.AleLrServicePortTypeWrapper;
import org.rifidi.edge.client.ale.models.aleserviceporttype.AleServicePortTypeWrapper;
import org.rifidi.edge.client.ale.models.ecspec.RemoteSpecModelWrapper;
import org.rifidi.edge.client.ale.models.enums.ConnectionStatus;
import org.rifidi.edge.client.ale.models.listeners.ConnectionChangeListener;
import org.rifidi.edge.client.ale.models.serviceprovider.SpecDataManager;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class AleTreeViewContentProvider implements ITreeContentProvider,
		ConnectionChangeListener, ISetChangeListener {

	@SuppressWarnings("unused")
	private Log logger = null;

	private AleServicePortTypeWrapper aWrapper = null;
	private AleLrServicePortTypeWrapper lWrapper = null;
	private AbstractTreeViewer viewer = null;
	private List<SpecDataManager> dataManagers = null;

	/**
	 * @param viewer
	 * @param aleTreeView
	 * 
	 */
	public AleTreeViewContentProvider() {
		logger = LogFactory.getLog(AleTreeViewContentProvider.class);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.
	 * Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof List){
			return ((List)parentElement).toArray();
		}

		if (parentElement instanceof SpecDataManager) {
			return ((SpecDataManager) parentElement).getSpecs();
			
		}

		// if(parentElement instanceof RemoteSpecModelWrapper){
		// return new Object[0]; //return info
		// }

		return new Object[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object
	 * )
	 */
	@Override
	public Object getParent(Object element) {
		if (element instanceof RemoteSpecModelWrapper) {
			return ((RemoteSpecModelWrapper) element).getParent();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.
	 * Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
		
		if (element instanceof AleServicePortTypeWrapper) {
			if (((AleServicePortTypeWrapper) element).isConnected())
				return true;
			else
				return false;
		}

		return false;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java
	 * .lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		aWrapper.removeConnectionChangeListener(this);
		aWrapper.removeSetChangeListener(this);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface
	 * .viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		if (this.viewer != viewer) {
			if (viewer instanceof AbstractTreeViewer) {
				this.viewer = (AbstractTreeViewer) viewer;
			} else {
				throw new RuntimeException(
						"ObservableTreeContentProvider supports AbstractTreeViewer but got "
								+ viewer.getClass());
			}
		}
		if (oldInput != newInput) {
			this.dataManagers = (List<SpecDataManager>) newInput;
			List<SpecDataManager> oldDataManagers = (List<SpecDataManager>) oldInput;
			if (oldDataManagers != null) {
				for (SpecDataManager specDataManager : oldDataManagers) {
					specDataManager.removeConnectionChangeListener(this);
					specDataManager.removeSetChangeListener(this);
				}
			}
			if (dataManagers != null) {
				for (SpecDataManager specDataManager : dataManagers) {
					specDataManager.addConnectionChangeListener(this);
					specDataManager.addSetChangeListener(this);
				}
			}
			viewer.refresh();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.ale.models.listeners.ConnectionChangeListener#
	 * connectionStatusChanged
	 * (org.rifidi.edge.client.ale.models.enums.ConnectionStatus)
	 */
	@Override
	public void connectionStatusChanged(ConnectionStatus status) {
		viewer.refresh();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.core.databinding.observable.set.ISetChangeListener#
	 * handleSetChange
	 * (org.eclipse.core.databinding.observable.set.SetChangeEvent)
	 */
	@Override
	public void handleSetChange(SetChangeEvent event) {
		viewer.refresh();

	}

}
