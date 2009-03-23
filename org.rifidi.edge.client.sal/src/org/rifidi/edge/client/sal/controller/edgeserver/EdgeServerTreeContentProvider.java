/**
 * 
 */
package org.rifidi.edge.client.sal.controller.edgeserver;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.List;

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
 * This is the content provider for the Edge Server Reader View
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class EdgeServerTreeContentProvider implements ITreeContentProvider,
		IMapChangeListener, PropertyChangeListener, EdgeServerController {

	/** The tree viewer associated with this content provider */
	private AbstractTreeViewer viewer = null;
	/** The logger for this class */
	private Log logger = LogFactory.getLog(EdgeServerTreeContentProvider.class);
	/** A static instance of this so we can use this object as a singleton */
	private static EdgeServerTreeContentProvider instance;
	/** The input Element */
	private List<RemoteEdgeServer> edgeServerList;

	/**
	 * Constructor called by eclipse
	 */
	public EdgeServerTreeContentProvider() {
		super();
		instance = this;
	}

	/**
	 * Static method to get the edge server controller
	 * 
	 * @return
	 */
	public static EdgeServerController getEdgeServerController() {
		return (EdgeServerController) instance;
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

		if (parentElement instanceof List) {
			return ((List) parentElement).toArray();
		} else if (parentElement instanceof RemoteEdgeServer) {

			RemoteEdgeServer server = (RemoteEdgeServer) parentElement;
			Collection<RemoteReader> readers = server.getRemoteReaders()
					.values();
			Object[] retVal = new Object[readers.size()];
			return readers.toArray(retVal);
		}
		return new Object[] {};
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
		if (element instanceof List) {
			return !((List) element).isEmpty();
		}
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
		logger.debug("inputElement is " + inputElement);

		return getChildren(inputElement);

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
	 * org.rifidi.edge.client.sal.controller.edgeserver.EdgeServerController
	 * #connect()
	 */
	@Override
	public void connect() {
		this.edgeServerList.get(0).connect();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.sal.controller.edgeserver.EdgeServerController
	 * #disconnect()
	 */
	@Override
	public void disconnect() {
		this.edgeServerList.get(0).disconnect();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.sal.controller.edgeserver.EdgeServerController
	 * #update()
	 */
	@Override
	public void update() {
		this.edgeServerList.get(0).update();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.sal.controller.edgeserver.EdgeServerController
	 * #saveConfiguration()
	 */
	@Override
	public void saveConfiguration() {
		this.edgeServerList.get(0).saveConfiguration();
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
			this.edgeServerList = (List<RemoteEdgeServer>) newInput;
			for (RemoteEdgeServer edgeServer : edgeServerList) {
				edgeServer.addPropertyChangeListener(this);
				edgeServer.getRemoteReaders().addMapChangeListener(this);
			}
			viewer.refresh();
		}

	}

	@Override
	public void handleMapChange(MapChangeEvent event) {

		// handle event when reader is swapped out
		for (Object key : event.diff.getChangedKeys()) {
			Object newVal = event.diff.getNewValue(key);
			Object oldVal = event.diff.getOldValue(key);
			if ((newVal instanceof RemoteReader)
					&& (oldVal instanceof RemoteReader)) {
				viewer.remove(oldVal);
				viewer.add(edgeServerList.get(0), newVal);
			}
		}

		// handle event when reader is added
		for (Object key : event.diff.getAddedKeys()) {
			Object val = event.diff.getNewValue(key);
			if (val instanceof RemoteReader) {
				viewer.add(edgeServerList.get(0), val);
				viewer.setExpandedState(edgeServerList.get(0), true);
			}
		}

		// handle event when reader is removed
		for (Object key : event.diff.getRemovedKeys()) {
			Object val = event.diff.getOldValue(key);
			if (val instanceof RemoteReader) {
				viewer.remove(val);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejava.beans.PropertyChangeListener#propertyChange(java.beans.
	 * PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		if (arg0.getPropertyName().equals(RemoteEdgeServer.STATE_PROPERTY)) {
			viewer.refresh(edgeServerList.get(0));
		}

	}

}
