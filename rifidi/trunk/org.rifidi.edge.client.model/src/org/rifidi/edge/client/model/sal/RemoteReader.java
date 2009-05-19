
package org.rifidi.edge.client.model.sal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.management.AttributeList;

import org.eclipse.core.databinding.observable.map.IMapChangeListener;
import org.eclipse.core.databinding.observable.map.MapChangeEvent;
import org.eclipse.core.databinding.observable.map.ObservableMap;
import org.eclipse.core.databinding.observable.map.WritableMap;
import org.eclipse.core.databinding.observable.set.ObservableSet;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.rifidi.edge.client.model.sal.commands.RequestExecuterSingleton;
import org.rifidi.edge.client.model.sal.properties.SessionStatePropertyBean;
import org.rifidi.edge.core.api.SessionStatus;
import org.rifidi.edge.core.api.rmi.dto.ReaderDTO;
import org.rifidi.edge.core.api.rmi.dto.SessionDTO;

/**
 * A model object that represents a reader on the server
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class RemoteReader extends AbstractAttributeContributorModelObject
		implements IMapChangeListener, PropertyChangeListener {

	/** The ID of the Reader */
	private ReaderDTO readerDTO;
	/** The set of remote session belonging to this reader */
	private ObservableMap remoteSessions;
	/** The set of tags that has been seen by this reader */
	private ObservableSet tags;
	/** The remote reader factory that created this reader */
	private RemoteReaderFactory factory;

	/**
	 * Constructor
	 * 
	 * @param readerDTO
	 *            The DTO of the reader
	 * @param factory
	 *            The RemoteReaderFactory that created this reader
	 */
	public RemoteReader(ReaderDTO readerDTO, RemoteReaderFactory factory) {
		super(readerDTO.getReaderID(), readerDTO.getAttributes());
		this.readerDTO = readerDTO;
		remoteSessions = new WritableMap();
		remoteSessions.addMapChangeListener(this);
		tags = new WritableSet();
		for (SessionDTO dto : readerDTO.getSessions()) {
			_addSession(new RemoteSession(readerDTO.getReaderID(), readerDTO
					.getReaderFactoryID(), dto));
		}
		this.factory = factory;
	}

	/**
	 * Returns the ID.  
	 * 
	 * @return the iD
	 */
	public String getID() {
		return readerDTO.getReaderID();
	}

	/**
	 * Returns the ID.  
	 * 
	 * @return the factory ID of this reader
	 */
	public String getFactoryID() {
		return this.readerDTO.getReaderFactoryID();
	}

	/**
	 * Helper method to add a session
	 * 
	 * @param session
	 *            The session to add
	 */
	private void _addSession(final RemoteSession session) {
		remoteSessions.getRealm().asyncExec(new Runnable() {
			@Override
			public void run() {
				remoteSessions.put(session.getSessionID(), session);
			}
		});
	}

	/**
	 * Returns the remoteSession.  
	 * 
	 * @return the remoteSessions
	 */
	public ObservableMap getRemoteSessions() {
		return remoteSessions;
	}

	/**
	 * Returns the set of tags.  
	 * 
	 * @return The tags seen by this reader
	 */
	public ObservableSet getTags() {
		return tags;
	}

	/**
	 * Returns the remote reader factory.  
	 * 
	 * @return the factory
	 */
	public RemoteReaderFactory getFactory() {
		return factory;
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.client.model.sal.AbstractAttributeContributorModelObject#doSynchAttributeChange(org.rifidi.edge.client.model.sal.RemoteEdgeServer, java.lang.String, javax.management.AttributeList)
	 */
	@Override
	protected void doSynchAttributeChange(RemoteEdgeServer server,
			String modelID, AttributeList list) {

		Command_SynchReaderPropertyChanges request = new Command_SynchReaderPropertyChanges(
				server, modelID, list);

		RequestExecuterSingleton.getInstance().scheduleRequest(request);

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.databinding.observable.map.IMapChangeListener#handleMapChange(org.eclipse.core.databinding.observable.map.MapChangeEvent)
	 */
	@Override
	public void handleMapChange(MapChangeEvent event) {
		// Add and remove listeners to sessions as they appear
		for (Object o : event.diff.getAddedKeys()) {
			RemoteSession session = (RemoteSession) event.diff.getNewValue(o);
			session.addPropertyChangeListener(this);
		}
		for (Object o : event.diff.getRemovedKeys()) {
			RemoteSession session = (RemoteSession) event.diff.getNewValue(o);
			if (session != null) {
				session.removePropertyChangeListener(this);
			}
		}
		for (Object o : event.diff.getChangedKeys()) {
			RemoteSession oldSession = (RemoteSession) event.diff
					.getOldValue(o);
			RemoteSession newSession = (RemoteSession) event.diff
					.getNewValue(o);
			if (oldSession != null) {
				oldSession.removePropertyChangeListener(this);
			}
			newSession.addPropertyChangeListener(this);
		}
		// if there are no more sessions, clear tag list
		if (this.remoteSessions.isEmpty()) {
			this.tags.clear();
		}

	}

	/*
	 * (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		if (arg0.getPropertyName().equals(
				SessionStatePropertyBean.SESSION_STATUS_PROPERTY)) {
			boolean atLeastOneSessionExecuting = false;

			// step through each session and see if there is at least on which
			// is executing
			for (Object obj : this.remoteSessions.values()) {
				RemoteSession session = (RemoteSession) obj;
				SessionStatus status = session.getStateOfSession();
				if (status == SessionStatus.PROCESSING
						|| status == SessionStatus.CONNECTING
						|| status == SessionStatus.LOGGINGIN) {
					atLeastOneSessionExecuting = true;
					break;

				}
			}
			if (!atLeastOneSessionExecuting) {
				tags.clear();
			}
		}

	}
}
