/*
 * RemoteSession.java
 * 
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.client.model.sal;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.core.databinding.observable.map.ObservableMap;
import org.eclipse.core.databinding.observable.map.WritableMap;
import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.api.rmi.dto.CommandDTO;
import org.rifidi.edge.api.rmi.dto.SessionDTO;
import org.rifidi.edge.client.model.sal.properties.SessionStatePropertyBean;

/**
 * Model object that represents a Session on the server
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class RemoteSession {

	/** The status of this session */
	private SessionStatus status;
	/** The DTO for this session */
	private SessionDTO sessionDTO;
	/** The ID of the reader associated with this reader */
	private String readerID;
	/** The factory this session belongs to */
	private String readerFactoryID;
	/** The property change support for this class */
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	/** The submitted commands */
	private ObservableMap remoteJobs;

	/**
	 * Constructor
	 * 
	 * @param readerID
	 *            The ID of the reader that the session belongs to
	 * @param sessionID
	 *            The ID of the session
	 */
	public RemoteSession(String readerID, String readerFactoryID,
			SessionDTO sessionDTO) {
		this.sessionDTO = sessionDTO;
		this.readerFactoryID = readerFactoryID;
		this.status = sessionDTO.getStatus();
		this.readerID = readerID;
		this.remoteJobs = new WritableMap();
		for (CommandDTO entry : sessionDTO.getCommands()) {
			_addRemoteJob(new RemoteJob(readerID, sessionDTO.getID(), entry
					.getProcessID(), entry.getCommandID()));
		}
	}

	/**
	 * Returns the state of the RemoteSession.
	 * 
	 * @return the stateOfSession
	 */
	public SessionStatus getStateOfSession() {
		return status;
	}

	/**
	 * Sets the state of the RemoteSession.
	 * 
	 * @param stateOfSession
	 *            the stateOfSession to set
	 */
	public void setStateOfSession(SessionStatus stateOfSession) {
		this.status = stateOfSession;
	}

	/**
	 * Returns the ID of the sessionDTO.
	 * 
	 * @return the sessionID
	 */
	public String getSessionID() {
		return sessionDTO.getID();
	}

	/**
	 * Returns the ID of the reader.
	 * 
	 * @return the readerID
	 */
	public String getReaderID() {
		return readerID;
	}

	/**
	 * Returns the ID of the readerFactory.
	 * 
	 * @return the readerFactoryID
	 */
	public String getReaderFactoryID() {
		return readerFactoryID;
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
	 * @return the The Remote Jobs that are currently running on the session
	 */
	public ObservableMap getRemoteJobs() {
		return remoteJobs;
	}

	/**
	 * helper method to add a remote job to the observable map
	 * 
	 * @param job
	 *            the job to add
	 */
	private void _addRemoteJob(final RemoteJob job) {
		remoteJobs.getRealm().asyncExec(new Runnable() {
			@Override
			public void run() {
				remoteJobs.put(job.getJobID(), job);
			}
		});
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
