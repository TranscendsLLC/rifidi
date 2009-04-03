/**
 * 
 */
package org.rifidi.edge.client.model.sal;

import org.eclipse.core.databinding.observable.map.ObservableMap;
import org.eclipse.core.databinding.observable.map.WritableMap;
import org.eclipse.core.databinding.observable.set.ObservableSet;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.rifidi.edge.core.api.rmi.dto.ReaderDTO;
import org.rifidi.edge.core.api.rmi.dto.SessionDTO;

/**
 * A model object that represents a reader on the server
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RemoteReader {

	/** The ID of the Reader */
	private ReaderDTO readerDTO;
	/** The set of remote session belonging to this reader */
	private ObservableMap remoteSessions;
	private ObservableSet tags;

	/**
	 * Constructor
	 * 
	 * @param readerDTO
	 *            The DTO of the reader
	 */
	public RemoteReader(ReaderDTO readerDTO) {
		super();
		this.readerDTO = readerDTO;
		remoteSessions = new WritableMap();
		tags = new WritableSet();
		for (SessionDTO dto : readerDTO.getSessions()) {
			_addSession(new RemoteSession(readerDTO.getReaderID(), readerDTO
					.getReaderFactoryID(), dto));
		}
	}

	/**
	 * @return the iD
	 */
	public String getID() {
		return readerDTO.getReaderID();
	}

	/**
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
	 * @return the remoteSessions
	 */
	public ObservableMap getRemoteSessions() {
		return remoteSessions;
	}

	/**
	 * @return The tags seen by this reader
	 */
	public ObservableSet getTags() {
		return tags;
	}
}
