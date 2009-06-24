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
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.IEvaluationService;
import org.rifidi.edge.client.model.sal.RemoteCommandConfiguration;
import org.rifidi.edge.client.model.sal.RemoteEdgeServer;
import org.rifidi.edge.client.model.sal.RemoteJob;
import org.rifidi.edge.client.model.sal.RemoteReader;
import org.rifidi.edge.client.model.sal.RemoteReaderFactory;
import org.rifidi.edge.client.model.sal.RemoteSession;
import org.rifidi.edge.client.model.sal.properties.RemoteObjectDirtyEvent;
import org.rifidi.edge.client.model.sal.properties.SessionStatePropertyBean;
import org.rifidi.edge.client.sal.views.tags.TagView;

/**
 * This is the content provider for the Edge Server Reader View
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class EdgeServerTreeContentProvider implements ITreeContentProvider,
		IMapChangeListener, PropertyChangeListener, EdgeServerController {

	/** The tree viewer associated with this content provider */
	private AbstractTreeViewer viewer = null;
	/** The logger for this class */
	private Log logger = LogFactory.getLog(EdgeServerTreeContentProvider.class);
	/** A static instance of this so we can use this object as a singleton */
	private static EdgeServerTreeContentProvider instance;
	/** The model Element */
	private RemoteEdgeServer remoteEdgeServer;

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
		this.remoteEdgeServer.connect();
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
		this.remoteEdgeServer.disconnect();
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
		this.remoteEdgeServer.update();
	}

	@Override
	public Set<RemoteReaderFactory> getReaderfactories() {
		return new HashSet<RemoteReaderFactory>(this.remoteEdgeServer
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
		this.remoteEdgeServer.saveConfiguration();
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
		this.remoteEdgeServer.createReader(factory, attributes);
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
		this.remoteEdgeServer.deleteReader(readerID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.sal.controller.edgeserver.EdgeServerController
	 * #createSession(java.lang.String)
	 */
	@Override
	public void createSession(String readerID) {
		this.remoteEdgeServer.createSession(readerID);

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
		this.remoteEdgeServer.deleteSession(readerID, sessionID);

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
		this.remoteEdgeServer.startSession(readerID, sessionID);

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
		this.remoteEdgeServer.stopSession(readerID, sessionID);

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
		this.remoteEdgeServer.deleteRemoteJob(job);

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
	public void submitCommand(RemoteSession session,
			RemoteCommandConfiguration configuration, Long interval) {
		if (interval <= (long) 0) {
			this.remoteEdgeServer.submitOneTimeCommand(session, configuration);
		} else {
			this.remoteEdgeServer.scheduleJob(session, configuration, interval);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.sal.controller.edgeserver.EdgeServerController
	 * #clearPropertyChanges(java.lang.String)
	 */
	@Override
	public void clearPropertyChanges(String readerID) {
		RemoteReader reader = (RemoteReader) this.remoteEdgeServer
				.getRemoteReaders().get(readerID);
		if (reader != null) {
			reader.clearUpdatedProperties();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.sal.controller.edgeserver.EdgeServerController
	 * #synchPropertyChanges(java.lang.String)
	 */
	@Override
	public void synchPropertyChanges(String readerID) {
		RemoteReader reader = (RemoteReader) this.remoteEdgeServer
				.getRemoteReaders().get(readerID);
		if (reader != null) {
			reader.synchUpdatedProperties(this.remoteEdgeServer);
		}

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

			List<RemoteEdgeServer> newInputList = (List<RemoteEdgeServer>) newInput;
			List<RemoteEdgeServer> oldInputList = (List<RemoteEdgeServer>) oldInput;
			if (oldInputList != null) {
				for (RemoteEdgeServer edgeServer : oldInputList) {
					edgeServer.removePropertyChangeListener(this);
					edgeServer.getRemoteReaders().removeMapChangeListener(this);
				}
			}
			if (newInputList.size() == 1) {
				this.remoteEdgeServer = newInputList.get(0);
				remoteEdgeServer.addPropertyChangeListener(this);
				remoteEdgeServer.getRemoteReaders().addMapChangeListener(this);
				// TODO: should we be listening for ReaderFactories?

				// ADD DRAG AND DROP SUPPORT
				EdgeServerDropTargetListener dropTargetListener = new EdgeServerDropTargetListener(
						remoteEdgeServer.getCommandConfigurations(), this,
						this.viewer);
				this.viewer.addDropSupport(DND.DROP_MOVE,
						new Transfer[] { TextTransfer.getInstance() },
						dropTargetListener);
			} else {
				remoteEdgeServer = null;
			}

			this.viewer.refresh();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.core.databinding.observable.map.IMapChangeListener#
	 * handleMapChange
	 * (org.eclipse.core.databinding.observable.map.MapChangeEvent)
	 */
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
		viewer.add(remoteEdgeServer, reader);
		viewer.setExpandedState(remoteEdgeServer, true);
		reader.addPropertyChangeListener(this);
	}

	/**
	 * Helper method to Remove a RemoteReader from the viewer
	 * 
	 * @param reader
	 */
	private void removeRemoteReader(RemoteReader reader) {
		reader.getRemoteSessions().removeMapChangeListener(this);
		reader.removePropertyChangeListener(this);
		viewer.remove(reader);
		IViewReference[] refs = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getViewReferences();
		for (IViewReference ref : refs) {
			if (ref.getId().equals(TagView.ID)
					&& ref.getSecondaryId().equals(reader.getID())) {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage().hideView(ref);
				break;
			}
		}

	}

	/**
	 * Add a session to the viewer
	 * 
	 * @param session
	 */
	private void addRemoteSession(RemoteSession session) {
		RemoteReader reader = (RemoteReader) remoteEdgeServer
				.getRemoteReaders().get(session.getReaderID());
		session.addPropertyChangeListener(this);
		session.getRemoteJobs().addMapChangeListener(this);
		viewer.add(reader, session);
		viewer.setExpandedState(reader, true);
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
		RemoteReader reader = (RemoteReader) remoteEdgeServer
				.getRemoteReaders().get(job.getReaderID());
		if (reader != null) {
			RemoteSession session = (RemoteSession) reader.getRemoteSessions()
					.get(job.getSessionID());
			if (session != null) {
				viewer.add(session, job);
				viewer.setExpandedState(session, true);
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
			viewer.refresh(remoteEdgeServer);
			IEvaluationService service = (IEvaluationService) PlatformUI
					.getWorkbench().getService(IEvaluationService.class);
			service
					.requestEvaluation("org.rifidi.edge.client.sal.edgeserver.state");
		} else if (arg0.getPropertyName().equals(
				SessionStatePropertyBean.SESSION_STATUS_PROPERTY)) {
			SessionStatePropertyBean bean = (SessionStatePropertyBean) arg0
					.getNewValue();
			RemoteReader reader = (RemoteReader) remoteEdgeServer
					.getRemoteReaders().get(bean.getReaderID());
			RemoteSession session = (RemoteSession) reader.getRemoteSessions()
					.get(bean.getSessionID());
			viewer.refresh(session);

		} else if (arg0.getPropertyName().equals(
				RemoteObjectDirtyEvent.DIRTY_EVENT_PROPERTY)) {
			RemoteObjectDirtyEvent bean = (RemoteObjectDirtyEvent) arg0
					.getNewValue();
			RemoteReader reader = (RemoteReader) remoteEdgeServer
					.getRemoteReaders().get(bean.getModelID());

			viewer.update(reader, null);

		}

	}
}
