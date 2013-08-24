/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.api;

import java.io.Serializable;
import java.util.List;


/**
 * A Data Transfer Object for Sessions. For serializing information about
 * Sessions.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class SessionDTO implements Serializable {

	/** SerialVersionID */
	private static final long serialVersionUID = 1L;
	/** The status of this session */
	private SessionStatus status;
	/** The Commands being executed. */
	private List<CommandDTO> commands;
	/** The ID of the session */
	private String ID;

	/**
	 * Constructor.
	 */
	public SessionDTO() {
	}

	/***
	 * Constructor
	 * 
	 * @param ID
	 *            The ID of the session
	 * @param status
	 *            The status of the session
	 * @param commands
	 *            The Commands executing on this session, both single-shot and
	 *            repeated commands
	 */
	public SessionDTO(String ID, SessionStatus status, List<CommandDTO> commands) {
		this.ID = ID;
		this.status = status;
		this.commands = commands;
	}

	/**
	 * Return the status of the session
	 * 
	 * @return the status of the session
	 */
	public SessionStatus getStatus() {
		return status;
	}

	/**
	 * 
	 * @return The set of commands executing on this session, both single-shot
	 *         and repeated
	 */
	public List<CommandDTO> getCommands() {
		return commands;
	}

	/**
	 * @return the ID of the Session
	 */
	public String getID() {
		return ID;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(SessionStatus status) {
		this.status = status;
	}

	/**
	 * @param commands
	 *            the commands to set
	 */
	public void setCommands(List<CommandDTO> commands) {
		this.commands = commands;
	}

	/**
	 * @param iD
	 *            the iD to set
	 */
	public void setID(String iD) {
		ID = iD;
	}
}
