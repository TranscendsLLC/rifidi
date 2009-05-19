
package org.rifidi.edge.client.twodview.sfx;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.databinding.observable.map.IMapChangeListener;
import org.eclipse.core.databinding.observable.map.MapChangeEvent;
import org.eclipse.core.databinding.observable.map.ObservableMap;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.swt.graphics.Image;
import org.rifidi.edge.client.model.sal.RemoteJob;
import org.rifidi.edge.client.model.sal.RemoteReader;
import org.rifidi.edge.client.model.sal.RemoteSession;
import org.rifidi.edge.client.model.sal.properties.SessionStatePropertyBean;
import org.rifidi.edge.client.twodview.Activator;
import org.rifidi.edge.core.api.SessionStatus;

/**
 * The readerID of this ReaderAlphaImageFigure should not change througout the
 * lifetime of it, although the model might be swapped in and out
 * 
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ReaderAlphaImageFigure extends AlphaImageFigure implements
		PropertyChangeListener, IMapChangeListener {

	/** The remote reader for this AlphaImage */
	private RemoteReader reader;
	private Set<RemoteSession> sessions;
	private ReaderState state;
	private final String readerID;

	/**
	 * Constructor. Must be called from within eclipse thread.
	 * After calling the constructor the setReader method must be called,
	 * to set the model (reader).
	 * 
	 * @param image
	 * @param readerID
	 */
	public ReaderAlphaImageFigure(Image image, String readerID) {
		super(image);
		this.readerID = readerID;
		this.sessions = new HashSet<RemoteSession>();
		// TODO: this might be deprecated...
		setToolTip(this.createToolTip());
//		addModel(reader);
//		computeState();
	}

	/**
	 * The readerID of this ReaderAlphaImageFigure. It will not change through
	 * the lifetime of this reader
	 * 
	 * @return
	 */
	public String getReaderId() {
		return readerID;
	}

	/**
	 * @return the reader
	 */
	public RemoteReader getReader() {
		return reader;
	}

	/**
	 * Used to swap the model out from the Alpha Image. If null is passed in,
	 * this ReaderAlphaImageFigure will have no underlying ReaderModel.
	 * 
	 * @param reader
	 * @return
	 */
	public void setReader(RemoteReader reader) {
		if (this.reader != null) {
			cleanupRemoteReader();
		}
		if (reader != null) {
			addModel(reader);
		}
		computeState();
	}

	/**
	 * Private helper method to add a model. Reader should not be null
	 * 
	 * @param reader
	 */
	private void addModel(RemoteReader reader) {
		this.reader = reader;
		ObservableMap map = this.reader.getRemoteSessions();
		map.addMapChangeListener(this);
		for (Object session : map.values()) {
			if (session instanceof RemoteSession) {
				addRemoteSession((RemoteSession) session);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.draw2d.Figure#getToolTip()
	 */
	@Override
	public IFigure getToolTip() {
		this.setToolTip(this.createToolTip());
		return super.getToolTip();
	}

	
	private IFigure createToolTip() {
		return new Label(this.readerID + "\n" + state);
	}

	/**
	 * Updates the onscreen status of the reader based on its state.  
	 * 
	 * @param state
	 */
	private void updateStatus(ReaderState state) {
		this.state = state;
		switch (state) {
		case UNKNOWN:
			this.setImage(Activator.getDefault().getImageRegistry().get(
					Activator.IMG_READER_UNKNOWN));
			break;
		case CONNECTED:
			this.setImage(Activator.getDefault().getImageRegistry().get(
					Activator.IMG_READER_ON));
			break;
		case WORKING:
			this.setImage(Activator.getDefault().getImageRegistry().get(
					Activator.IMG_READER_WORKING));
			break;
		case CONNECTING:
			this.setImage(Activator.getDefault().getImageRegistry().get(
					Activator.IMG_READER_CONNECTING));
			break;
		case DISCONNECTED:
			this.setImage(Activator.getDefault().getImageRegistry().get(
					Activator.IMG_READER_OFF));
			break;
		}
	}

	/**
	 * This method computes the state of a RemoteReader. Because the state of
	 * the remote reader is actually the state of the sessions, a simple
	 * calculation is needed to condense the state of the session down into one
	 * state of the reader. The state works like this: If there is at least one
	 * session in the connected state, then the reader is in the OK state.
	 */
	private void computeState() {
		boolean connected = false;
		boolean connecting = false;
		boolean working = false;
		if (this.reader == null) {
			updateStatus(ReaderState.UNKNOWN);
			return;
		}
		for (RemoteSession s : this.sessions) {
			if (s.getStateOfSession().equals(SessionStatus.PROCESSING)) {
				connected = true;
				if (!s.getRemoteJobs().isEmpty()) {
					working = true;
				}
				break;
			}
			if (s.getStateOfSession().equals(SessionStatus.LOGGINGIN)
					|| s.getStateOfSession().equals(SessionStatus.CONNECTING)) {
				connecting = true;
			}
		}
		if (working) {
			updateStatus(ReaderState.WORKING);
			return;
		}
		if (connected) {
			updateStatus(ReaderState.CONNECTED);
			return;
		}
		if (connecting) {
			updateStatus(ReaderState.CONNECTING);
			return;
		}
		updateStatus(ReaderState.DISCONNECTED);
	}

	/*
	 * (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(
				SessionStatePropertyBean.SESSION_STATUS_PROPERTY)) {
			computeState();
		}

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.databinding.observable.map.IMapChangeListener#handleMapChange(org.eclipse.core.databinding.observable.map.MapChangeEvent)
	 */
	@Override
	public void handleMapChange(MapChangeEvent event) {
		// handle event when obj is swapped out
		for (Object key : event.diff.getChangedKeys()) {
			Object newVal = event.diff.getNewValue(key);
			Object oldVal = event.diff.getOldValue(key);
			if ((newVal instanceof RemoteSession)
					&& (oldVal instanceof RemoteSession)) {
				removeRemoteSession((RemoteSession) oldVal);
				addRemoteSession((RemoteSession) newVal);
			} else if (newVal instanceof RemoteJob) {
				computeState();
			}
		}

		// handle event when obj is added
		for (Object key : event.diff.getAddedKeys()) {
			Object val = event.diff.getNewValue(key);
			if (val instanceof RemoteSession) {
				addRemoteSession((RemoteSession) val);
			}
			if (val instanceof RemoteJob) {
				computeState();
			}

		}

		// handle event when obj is removed
		for (Object key : event.diff.getRemovedKeys()) {
			Object val = event.diff.getOldValue(key);
			if (val instanceof RemoteSession) {
				removeRemoteSession((RemoteSession) val);
			}
			if (val instanceof RemoteJob) {
				computeState();
			}
		}

	}

	/**
	 * Helper method to process the event when a session is added
	 * 
	 * @param newVal
	 */
	private void addRemoteSession(RemoteSession newVal) {
		RemoteSession session = (RemoteSession) newVal;
		session.addPropertyChangeListener(this);
		session.getRemoteJobs().addMapChangeListener(this);
		sessions.add(session);
		computeState();

	}

	/**
	 * Helper method to process the event when a session is removed
	 * 
	 * @param oldVal
	 */
	private void removeRemoteSession(RemoteSession oldVal) {
		RemoteSession session = (RemoteSession) oldVal;
		session.removePropertyChangeListener(this);
		session.getRemoteJobs().removeMapChangeListener(this);
		sessions.remove((RemoteSession) oldVal);
		computeState();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.client.twodview.sfx.AlphaImageFigure#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		if (reader != null) {
			cleanupRemoteReader();
		}
	}

	/**
	 * Helper method to clean up the Remote reader when it is set to null
	 */
	private void cleanupRemoteReader() {
		this.reader.getRemoteSessions().removeMapChangeListener(this);
		this.reader.removePropertyChangeListener(this);
		Set<RemoteSession> sessionsToRemove = new HashSet<RemoteSession>(
				this.sessions);
		for (RemoteSession session : sessionsToRemove) {
			removeRemoteSession(session);
		}
		this.reader = null;
	}

	/**
	 * States for the Reader. We should add one that tells us if a job is
	 * executing on the reader
	 * 
	 * @author Kyle Neumeier - kyle@pramari.com
	 * 
	 */
	private enum ReaderState {
		/** If the RemoteReader is connected */
		CONNECTED,
		/** If the remote reader is disconnected */
		DISCONNECTED,
		/** If the remote reader is connecting */
		CONNECTING,
		/** If the remote reader is executing a command */
		WORKING,
		/** If the remote reader is not avaiable */
		UNKNOWN
	}

}
