/**
 * 
 */
package org.rifidi.edge.client.sal.views;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.map.IMapChangeListener;
import org.eclipse.core.databinding.observable.map.MapChangeEvent;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.rifidi.edge.client.model.sal.RemoteEdgeServer;
import org.rifidi.edge.client.model.sal.RemoteReader;

/**
 * @author kyle
 * 
 */
public class EdgeServerTreeContentProvider implements ITreeContentProvider,
		IMapChangeListener {

	/** The tree viewer associated with this content provider */
	private AbstractTreeViewer viewer = null;
	private Log logger = LogFactory.getLog(EdgeServerTreeContentProvider.class);
	private RemoteEdgeServer edgeServer=null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.
	 * Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof RemoteEdgeServer) {
			RemoteEdgeServer server = (RemoteEdgeServer) parentElement;
			Collection<RemoteReader> readers = server.getRemoteReaders()
					.values();
			Object[] retVal = new Object[readers.size()];
			readers.toArray(retVal);
		}
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
		// TODO Auto-generated method stub
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
		if (element instanceof RemoteEdgeServer) {
			RemoteEdgeServer server = (RemoteEdgeServer) element;
			Collection<RemoteReader> readers = server.getRemoteReaders()
					.values();
			if (readers.size() > 0)
				return true;
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
		if (inputElement instanceof RemoteEdgeServer) {
			((RemoteEdgeServer) inputElement).getRemoteReaders()
					.addMapChangeListener(this);
		}
		return new Object[] { inputElement };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

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
			this.edgeServer=(RemoteEdgeServer)newInput;
			viewer.refresh();
		}

	}

	@Override
	public void handleMapChange(MapChangeEvent event) {
		for(Object key : event.diff.getAddedKeys()){
			Object val = event.diff.getNewValue(key);
			if(val instanceof RemoteReader){
				viewer.add(edgeServer, val);
			}
		}

	}

}
