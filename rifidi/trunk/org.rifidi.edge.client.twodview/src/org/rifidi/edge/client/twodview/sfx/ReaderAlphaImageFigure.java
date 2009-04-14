/* 
 *  ReaderAlphaImageFigure.java
 *  Created:	Sep 4, 2008
 *  Project:	RiFidi Dashboard - An RFID infrastructure monitoring tool
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
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
import org.rifidi.edge.client.model.sal.RemoteReader;
import org.rifidi.edge.client.model.sal.RemoteSession;
import org.rifidi.edge.client.model.sal.properties.SessionStatePropertyBean;
import org.rifidi.edge.client.twodview.Activator;
import org.rifidi.edge.core.api.SessionStatus;

/**
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

	/**
	 * Constructor. Must be called from within eclipse thread
	 * 
	 * @param image
	 * @param reader
	 */
	public ReaderAlphaImageFigure(Image image, RemoteReader reader) {
		super(image);
		this.reader = reader;
		this.sessions = new HashSet<RemoteSession>();
		ObservableMap map = this.reader.getRemoteSessions();
		map.addMapChangeListener(this);
		for (Object session : map.values()) {
			if (session instanceof RemoteSession) {
				addRemoteSession((RemoteSession) session);
			}
		}

		// TODO: this might be deprecated...
		setToolTip(this.createToolTip());
		computeState();
	}

	/**
	 * @return the reader
	 */
	public RemoteReader getReader() {
		return reader;
	}

	@Override
	public IFigure getToolTip() {
		this.setToolTip(this.createToolTip());
		return super.getToolTip();
	}

	public String getReaderId() {
		return this.reader.getID();
	}

	private IFigure createToolTip() {
		Label ttl = null;
		if (reader == null) {
			ttl = new Label("I AM JUST A DUMMY\nsorry, no status here");
		} else {
			ttl = new Label(reader.getID() + "\n" + state);
		}
		return ttl;
	}

	private void updateStatus(ReaderState state) {
		this.state = state;
		switch (state) {
		case CONNECTED:
			this.setImage(Activator.getDefault().getImageRegistry().get(
					Activator.IMG_READER_ON));
			break;
		case CONNECTING:
			// TODO need a better image for this
			this.setImage(Activator.getDefault().getImageRegistry().get(
					Activator.IMG_READER));
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
		for (RemoteSession s : this.sessions) {
			if (s.getStateOfSession().equals(SessionStatus.PROCESSING)) {
				connected = true;
				break;
			}
			if (s.getStateOfSession().equals(SessionStatus.LOGGINGIN)) {
				connecting = true;
			}
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

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(
				SessionStatePropertyBean.SESSION_STATUS_PROPERTY)) {
			computeState();
		}

	}

	@Override
	public void handleMapChange(MapChangeEvent event) {
		// handle event when reader is swapped out
		for (Object key : event.diff.getChangedKeys()) {
			Object newVal = event.diff.getNewValue(key);
			Object oldVal = event.diff.getOldValue(key);
			if ((newVal instanceof RemoteSession)
					&& (oldVal instanceof RemoteSession)) {
				removeRemoteSession((RemoteSession) oldVal);
				addRemoteSession((RemoteSession) newVal);
			}
		}

		// handle event when reader is added
		for (Object key : event.diff.getAddedKeys()) {
			Object val = event.diff.getNewValue(key);
			if (val instanceof RemoteSession) {
				addRemoteSession((RemoteSession) val);
			}

		}

		// handle event when reader is removed
		for (Object key : event.diff.getRemovedKeys()) {
			Object val = event.diff.getOldValue(key);
			if (val instanceof RemoteSession) {
				removeRemoteSession((RemoteSession) val);
			}
		}

	}

	/**
	 * Helper method to process the event when a session is added
	 * 
	 * @param newVal
	 */
	private void addRemoteSession(RemoteSession newVal) {
		((RemoteSession) newVal).addPropertyChangeListener(this);
		sessions.add((RemoteSession) newVal);

	}

	/**
	 * Helper method to process the event when a session is removed
	 * 
	 * @param oldVal
	 */
	private void removeRemoteSession(RemoteSession oldVal) {
		((RemoteSession) oldVal).removePropertyChangeListener(this);
		sessions.remove((RemoteSession) oldVal);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.client.twodview.sfx.AlphaImageFigure#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		this.reader.getRemoteSessions().removeMapChangeListener(this);
		Set<RemoteSession> sessionsToRemove = new HashSet<RemoteSession>(
				this.sessions);
		for (RemoteSession session : sessionsToRemove) {
			removeRemoteSession(session);
		}
	}

	/**
	 * States for the Reader. We should add one that tells us if a job is
	 * executing on the reader
	 * 
	 * @author Kyle Neumeier - kyle@pramari.com
	 * 
	 */
	private enum ReaderState {
		CONNECTED, DISCONNECTED, CONNECTING
	}

}
