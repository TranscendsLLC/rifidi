package org.rifidi.edge.api.rmi.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.rifidi.edge.api.SessionStatus;

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
	/** The Commands being executed. Integer is processID, String is command ID */
	private Set<CommandDTO> commands;
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
	 *            The Commands being executed. Integer is processID, String is
	 *            command ID
	 */
	public SessionDTO(String ID, SessionStatus status, Set<CommandDTO> commands) {
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
	 * The set of jobs that are currently scheduled on the session
	 * 
	 * @return
	 */
	public Set<CommandDTO> getCommands() {
		return commands;
	}

	/**
	 * @return the iD
	 */
	public String getID() {
		return ID;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(SessionStatus status) {
		this.status = status;
	}

	/**
	 * @param commands the commands to set
	 */
	public void setCommands(Set<CommandDTO> commands) {
		this.commands = commands;
	}

	/**
	 * @param iD the iD to set
	 */
	public void setID(String iD) {
		ID = iD;
	}

}
