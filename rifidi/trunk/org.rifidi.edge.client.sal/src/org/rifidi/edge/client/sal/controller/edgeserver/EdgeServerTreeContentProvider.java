/**
 * 
 */
package org.rifidi.edge.client.sal.controller.edgeserver;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.management.AttributeList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.map.IMapChangeListener;
import org.eclipse.core.databinding.observable.map.MapChangeEvent;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.rifidi.edge.client.model.sal.RemoteCommandConfiguration;
import org.rifidi.edge.client.model.sal.RemoteEdgeServer;
import org.rifidi.edge.client.model.sal.RemoteJob;
import org.rifidi.edge.client.model.sal.RemoteReader;
import org.rifidi.edge.client.model.sal.RemoteReaderFactory;
import org.rifidi.edge.client.model.sal.RemoteSession;
import org.rifidi.edge.client.model.sal.properties.SessionStatePropertyBean;

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
		} else if (parentElement instanceof RemoteReader) {
			RemoteReader reader = (RemoteReader) parentElement;
			Collection<RemoteSession> sessions = reader.getRemoteSessions()
					.values();
			Object[] retVal = new Object[sessions.size()];
			return sessions.toArray(retVal);
		} else if (parentElement instanceof RemoteSession) {
			RemoteSession session = (RemoteSession) parentElement;
			Collection<RemoteJob> jobs = session.getRemoteJobs().values();
			Object[] retVal = new Object[jobs.size()];
			return jobs.toArray(retVal);
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
		} else if (element instanceof RemoteEdgeServer) {
			RemoteEdgeServer server = (RemoteEdgeServer) element;
			Collection<RemoteReader> readers = server.getRemoteReaders()
					.values();
			if (readers.size() > 0)
				return true;
		} else if (element instanceof RemoteReader) {
			RemoteReader reader = (RemoteReader) element;
			return !reader.getRemoteSessions().isEmpty();
		} else if (element instanceof RemoteSession) {
			RemoteSession session = (RemoteSession) element;
			return !session.getRemoteJobs().isEmpty();
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

	@Override
	public Set<RemoteReaderFactory> getReaderfactories() {
		return new HashSet<RemoteReaderFactory>(this.edgeServerList.get(0)
				.getReaderFactories().values());
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
	 * org.rifidi.edge.client.sal.controller.edgeserver.EdgeServerController
	 * #createReader(org.rifidi.edge.client.model.sal.RemoteReaderFactory,
	 * javax.management.AttributeList)
	 */
	@Override
	public void createReader(RemoteReaderFactory factory,
			AttributeList attributes) {
		this.edgeServerList.get(0).createReader(factory, attributes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.sal.controller.edgeserver.EdgeServerController
	 * #deleteReader(java.lang.String)
	 */
	@Override
	public void deleteReader(String readerID) {
		this.edgeServerList.get(0).deleteReader(readerID);
	}

	@Override
	public void createSession(String readerID) {
		this.edgeServerList.get(0).createSession(readerID);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.sal.controller.edgeserver.EdgeServerController
	 * #deleteSession(java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteSession(String readerID, String sessionID) {
		this.edgeServerList.get(0).deleteSession(readerID, sessionID);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.sal.controller.edgeserver.EdgeServerController
	 * #startSession(java.lang.String, java.lang.String)
	 */
	@Override
	public void startSession(String readerID, String sessionID) {
		this.edgeServerList.get(0).startSession(readerID, sessionID);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.sal.controller.edgeserver.EdgeServerController
	 * #stopSession(java.lang.String, java.lang.String)
	 */
	@Override
	public void stopSession(String readerID, String sessionID) {
		this.edgeServerList.get(0).stopSession(readerID, sessionID);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.sal.controller.edgeserver.EdgeServerController
	 * #deleteRemoteJob(org.rifidi.edge.client.model.sal.RemoteJob)
	 */
	@Override
	public void deleteRemoteJob(RemoteJob job) {
		this.edgeServerList.get(0).deleteRemoteJob(job);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.sal.controller.edgeserver.EdgeServerController
	 * #scheduleJob(org.rifidi.edge.client.model.sal.RemoteSession,
	 * org.rifidi.edge.client.model.sal.RemoteCommandConfiguration,
	 * java.lang.Long)
	 */
	@Override
	public void scheduleJob(RemoteSession session,
			RemoteCommandConfiguration configuration, Long interval) {
		this.edgeServerList.get(0)
				.scheduleJob(session, configuration, interval);

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
			List<RemoteEdgeServer> oldEdgeServerList = (List<RemoteEdgeServer>) oldInput;
			if (oldEdgeServerList != null) {
				for (RemoteEdgeServer edgeServer : oldEdgeServerList) {
					edgeServer.removePropertyChangeListener(this);
					edgeServer.getRemoteReaders().removeMapChangeListener(this);
				}
			}
			if (edgeServerList != null) {
				for (RemoteEdgeServer edgeServer : edgeServerList) {
					edgeServer.addPropertyChangeListener(this);
					edgeServer.getRemoteReaders().addMapChangeListener(this);
					// TODO: should we be listening for ReaderFactories?
				}
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
				removeRemoteReader((RemoteReader) oldVal);
				addRemoteReader((RemoteReader) newVal);
			} else if (newVal instanceof RemoteSession) {
				logger.debug("SESSION CHANGED!");
			} else if (newVal instanceof RemoteJob) {
				logger.debug("JOB CHANGED!");
			}
		}

		// handle event when reader is added
		for (Object key : event.diff.getAddedKeys()) {
			Object val = event.diff.getNewValue(key);
			if (val instanceof RemoteReader) {
				addRemoteReader((RemoteReader) val);
			} else if (val instanceof RemoteSession) {
				addRemoteSession((RemoteSession) val);
			} else if (val instanceof RemoteJob) {
				addRemoteJob((RemoteJob) val);
			}
		}

		// handle event when reader is removed
		for (Object key : event.diff.getRemovedKeys()) {
			Object val = event.diff.getOldValue(key);
			if (val instanceof RemoteReader) {
				removeRemoteReader((RemoteReader) val);
			} else if (val instanceof RemoteSession) {
				removeRemoteSession((RemoteSession) val);
			} else if (val instanceof RemoteJob) {
				removeRemoteJob((RemoteJob) val);
			}
		}

	}

	/**
	 * Helper method to add a RemoteReader to the viewer
	 * 
	 * @param reader
	 */
	private void addRemoteReader(RemoteReader reader) {
		reader.getRemoteSessions().addMapChangeListener(this);
		viewer.add(edgeServerList.get(0), reader);
		viewer.setExpandedState(edgeServerList.get(0), true);
	}

	/**
	 * Helper method to Remove a RemoteReader from the viewer
	 * 
	 * @param reader
	 */
	private void removeRemoteReader(RemoteReader reader) {
		reader.getRemoteSessions().removeMapChangeListener(this);
		viewer.remove(reader);
	}

	/**
	 * Add a session to the viewer
	 * 
	 * @param session
	 */
	private void addRemoteSession(RemoteSession session) {
		RemoteReader reader = (RemoteReader) edgeServerList.get(0)
				.getRemoteReaders().get(session.getReaderID());
		session.addPropertyChangeListener(this);
		session.getRemoteJobs().addMapChangeListener(this);
		viewer.add(reader, session);
	}

	/**
	 * Helper method to remove a remote session from the viewer
	 * 
	 * @param session
	 */
	private void removeRemoteSession(RemoteSession session) {
		session.removePropertyChangeListener(this);
		session.getRemoteJobs().removeMapChangeListener(this);
		viewer.remove(session);
	}

	/**
	 * Helper method to add a RemoteJob
	 * 
	 * @param job
	 */
	private void addRemoteJob(RemoteJob job) {
		RemoteReader reader = (RemoteReader) edgeServerList.get(0)
				.getRemoteReaders().get(job.getReaderID());
		if (reader != null) {
			RemoteSession session = (RemoteSession) reader.getRemoteSessions()
					.get(job.getSessionID());
			if (session != null) {
				viewer.add(session, job);
			}
		}
	}

	/**
	 * Helper method to remove a remote job
	 * 
	 * @param job
	 */
	private void removeRemoteJob(RemoteJob job) {
		viewer.remove(job);
	}

	/*
	 * 
	 * Must be called from within eclipse thread! (non-Javadoc)
	 * 
	 * @seejava.beans.PropertyChangeListener#propertyChange(java.beans.
	 * PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		if (arg0.getPropertyName().equals(RemoteEdgeServer.STATE_PROPERTY)) {
			viewer.refresh(edgeServerList.get(0));
		} else if (arg0.getPropertyName().equals(
				SessionStatePropertyBean.SESSION_STATUS_PROPERTY)) {
			SessionStatePropertyBean bean = (SessionStatePropertyBean) arg0
					.getNewValue();
			RemoteReader reader = (RemoteReader) edgeServerList.get(0)
					.getRemoteReaders().get(bean.getReaderID());
			RemoteSession session = (RemoteSession) reader.getRemoteSessions()
					.get(bean.getSessionID());
			viewer.refresh(session);

		}

	}

}
