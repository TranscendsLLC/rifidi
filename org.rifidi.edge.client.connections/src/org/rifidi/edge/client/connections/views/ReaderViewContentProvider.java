package org.rifidi.edge.client.connections.views;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.rifidi.edge.client.connections.edgeserver.EdgeServerConnection;
import org.rifidi.edge.client.connections.registryservice.EdgeServerConnectionRegistryService;
import org.rifidi.edge.client.connections.registryservice.RemoteReaderConnectionRegistryService;
import org.rifidi.edge.client.connections.registryservice.impl.EdgeServerConnectionRegistryServiceImpl;
import org.rifidi.edge.client.connections.registryservice.listeners.EdgeServerConnectionRegistryListener;
import org.rifidi.edge.client.connections.registryservice.listeners.RemoteReaderConnectionRegistryListener;
import org.rifidi.edge.client.connections.remotereader.RemoteReader;
import org.rifidi.edge.client.connections.remotereader.RemoteReaderID;
import org.rifidi.edge.client.connections.remotereader.listeners.ReaderStateListener;
import org.rifidi.edge.core.rmi.client.sessionstub.valueobjects.ReaderInfoWrapper;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

public class ReaderViewContentProvider implements ITreeContentProvider,
		EdgeServerConnectionRegistryListener,
		RemoteReaderConnectionRegistryListener, ReaderStateListener {

	private TreeViewer treeViewer;
	private EdgeServerConnectionRegistryService edgeServerConnectionRegistryService;
	private RemoteReaderConnectionRegistryService readerConnectionRegistryService;
	@SuppressWarnings("unused")
	private Log logger = LogFactory.getLog(ReaderViewContentProvider.class);
	private Set<RemoteReader> remoteReaders;

	public ReaderViewContentProvider() {
		ServiceRegistry.getInstance().service(this);
		remoteReaders = new HashSet<RemoteReader>();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof EdgeServerConnectionRegistryServiceImpl) {
			return ((EdgeServerConnectionRegistryServiceImpl) parentElement)
					.getConnections();
		}
		if (parentElement instanceof EdgeServerConnection) {
			Set<RemoteReaderID> ids = ((EdgeServerConnection) parentElement)
					.getRemoteReaderIDs();
			RemoteReader[] retVal = new RemoteReader[ids.size()];
			Iterator<RemoteReaderID> iter = ids.iterator();
			for (int i = 0; i < retVal.length; i++) {
				retVal[i] = this.readerConnectionRegistryService
						.getRemoteReader(iter.next());
			}
			return retVal;
		}
		if (parentElement instanceof RemoteReader) {
			RemoteReader rr = (RemoteReader) parentElement;
			ReaderInfoWrapper riw;
			riw = rr.getReaderInfo();
			Object[] retVal = new Object[4];
			retVal[0] = "IPAddress: " + riw.getValue("IPAddress");
			retVal[1] = "Port: " + riw.getValue("port");
			retVal[2] = "Reader Type: "
					+ rr.getReaderPluginWrapper().getReaderName();
			retVal[3] = "ReaderID: " + rr.getID();
			return retVal;
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		// if(element instanceof )
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof EdgeServerConnectionRegistryServiceImpl) {
			if (((EdgeServerConnectionRegistryService) element)
					.getConnectionIDs().size() > 0) {
				return true;
			} else {
				return false;
			}
		}
		if (element instanceof EdgeServerConnection) {
			EdgeServerConnection edgeServerConnection = ((EdgeServerConnection) element);
			if (edgeServerConnection.isConnected()
					&& edgeServerConnection.getRemoteReaderIDs().size() > 0) {

				return true;
			} else {
				return false;
			}
		}
		if (element instanceof RemoteReader) {
			return true;
		}
		return false;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof EdgeServerConnectionRegistryServiceImpl) {
			return ((EdgeServerConnectionRegistryServiceImpl) inputElement)
					.getConnections();
		}
		return null;
	}

	@Override
	public void dispose() {
		readerConnectionRegistryService.removeListener(this);
		edgeServerConnectionRegistryService.removeListener(this);
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (viewer instanceof TreeViewer)
			this.treeViewer = (TreeViewer) viewer;
	}

	@Override
	public void serverAdded(EdgeServerConnection server) {
		treeViewer.refresh();
	}

	@Override
	public void serverRemoved(EdgeServerConnection server) {
		treeViewer.refresh();
	}

	@Override
	public void readerAdded(RemoteReader remoteReader) {
		if (!remoteReaders.contains(remoteReader)) {
			remoteReaders.add(remoteReader);
			remoteReader.addStateListener(this);
			treeViewer.refresh();
		}

	}

	@Override
	public void readerRemoved(RemoteReader remoteReader) {
		if (remoteReaders.contains(remoteReader)) {
			remoteReaders.remove(remoteReader);
			remoteReader.removeStateListener(this);
			treeViewer.refresh();
		}
	}

	@Inject
	public void setEdgeServerConnectionRegistryService(
			EdgeServerConnectionRegistryService edgeServerConnectionRegistryService) {
		this.edgeServerConnectionRegistryService = edgeServerConnectionRegistryService;
		edgeServerConnectionRegistryService.addListener(this);
	}

	@Inject
	public void setRemoteReaderConnectionRegistryService(
			RemoteReaderConnectionRegistryService remoteReaderConnectionRegistryService) {
		this.readerConnectionRegistryService = remoteReaderConnectionRegistryService;
		remoteReaderConnectionRegistryService.addListener(this);
	}

	@Override
	public void readerStateChanged(RemoteReaderID readerID, String newState) {
		ISelection sel = treeViewer.getSelection();
		if (sel instanceof TreeSelection) {
			Object obj = ((TreeSelection) sel).getFirstElement();
			if (obj instanceof RemoteReader) {
				RemoteReader selectedReader = (RemoteReader) obj;
				if (selectedReader.getID() == readerID) {
					// TODO: doesn't work right
					// logger.debug("Setting selection to null");
					// treeViewer.setSelection(null, false);
					// logger.debug("Setting selection to " + sel);
					// treeViewer.setSelection(sel, true);
				}
			}
		}

	}

}
