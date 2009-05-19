
package org.rifidi.edge.client.model.sal;

/**
 * This represents a command that has been submitted to a session
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RemoteJob {

	/**The reader ID this job is running on*/
	private String readerID;
	/**The session ID this job is running on*/
	private String sessionID;
	/**The ID of this job*/
	private Integer jobID;
	/**The ID of the commandConfiguration of this job*/
	private String commandConfigurationID;

	/**
	 * @param readerID
	 * @param sessionID
	 * @param jobID
	 * @param commandConfigurationID
	 */
	public RemoteJob(String readerID, String sessionID, Integer jobID,
			String commandConfigurationID) {
		this.readerID = readerID;
		this.sessionID = sessionID;
		this.jobID = jobID;
		this.commandConfigurationID = commandConfigurationID;
	}

	/**
	 * @return the readerID
	 */
	public String getReaderID() {
		return readerID;
	}

	/**
	 * @return the sessionID
	 */
	public String getSessionID() {
		return sessionID;
	}

	/**
	 * @return the jobID
	 */
	public Integer getJobID() {
		return jobID;
	}

	/**
	 * @return the commandConfigurationID
	 */
	public String getCommandConfigurationID() {
		return commandConfigurationID;
	}
}
