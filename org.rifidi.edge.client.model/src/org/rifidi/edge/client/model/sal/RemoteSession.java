/**
 * 
 */
package org.rifidi.edge.client.model.sal;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.rifidi.edge.client.model.sal.properties.SessionStatePropertyBean;
import org.rifidi.edge.core.api.SessionStatus;
import org.rifidi.edge.core.api.rmi.dto.SessionDTO;

/**
 * Model object that represents a Session on the server
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RemoteSession {

	/** The status of this session */
	private SessionStatus status;
	/** The DTO for this session */
	private SessionDTO sessionDTO;
	/** The ID of the reader associated with this reader */
	private String readerID;
	/** The property change support for this class */
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	/**
	 * Constructor
	 * 
	 * @param readerID
	 *            The ID of the reader that the session belongs to
	 * @param sessionID
	 *            The ID of the session
	 */
	public RemoteSession(String readerID, SessionDTO sessionDTO) {
		super();
		this.sessionDTO = sessionDTO;
		this.status = sessionDTO.getStatus();
		this.readerID = readerID;
	}

	/**
	 * @return the stateOfSession
	 */
	public SessionStatus getStateOfSession() {
		return status;
	}

	/**
	 * @param stateOfSession
	 *            the stateOfSession to set
	 */
	public void setStateOfSession(SessionStatus stateOfSession) {
		this.status = stateOfSession;
	}

	/**
	 * @return the sessionID
	 */
	public String getSessionID() {
		return sessionDTO.getID();
	}

	/**
	 * @return the readerID
	 */
	public String getReaderID() {
		return readerID;
	}

	/**
	 * Add a property change listener
	 * 
	 * @param listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	/**
	 * Remove the property change listener
	 * 
	 * @param listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}

	/**
	 * Must be called from within eclipse thread!
	 * 
	 * @param status
	 */
	protected void setStatus(SessionStatus status) {
		SessionStatus oldStatus = this.status;
		this.status = status;
		SessionStatePropertyBean oldProp = new SessionStatePropertyBean(
				getReaderID(), getSessionID(), oldStatus);
		SessionStatePropertyBean newProp = new SessionStatePropertyBean(
				getReaderID(), getSessionID(), status);
		pcs.firePropertyChange(
				SessionStatePropertyBean.SESSION_STATUS_PROPERTY, oldProp,
				newProp);
	}

}
