
package org.rifidi.edge.core.notifications;

import org.rifidi.edge.core.api.SessionStatus;

/**
 * TODO: Class level comment.  
 * 
 * @author kyle
 */
public interface NotifierService {

	/**
	 * TODO: Method level comment.  
	 * 
	 * @param readerID
	 * @param sessionID
	 */
	public void addSessionEvent(String readerID, String sessionID);

	/**
	 * TODO: Method level comment. 
	 * 
	 * @param readerID
	 * @param sessionID
	 */
	public void removeSessionEvent(String readerID, String sessionID);

	/**
	 * TODO: Method level comment. 
	 * 
	 * @param serviceID
	 */
	public void addConfigurationEvent(String serviceID);

	/**
	 * TODO: Method level comment. 
	 * 
	 * @param readerID
	 */
	public void removeConfigurationEvent(String readerID);

	/**
	 * TODO: Method level comment. 
	 * 
	 * @param readerFactoryID
	 */
	public void addReaderFactoryEvent(String readerFactoryID);

	/**
	 * TODO: Method level comment. 
	 * 
	 * @param readerFactoryID
	 */
	public void removeReaderFactoryEvent(String readerFactoryID);

	/**
	 * TODO: Method level comment. 
	 * 
	 * @param readerID
	 * @param sessionID
	 * @param sessionStatus
	 */
	public void sessionStatusChanged(String readerID, String sessionID,
			SessionStatus sessionStatus);

	/**
	 * TODO: Method level comment. 
	 * 
	 * @param readerFactoryID
	 */
	public void addCommandConfigFactoryEvent(String readerFactoryID);

	/**
	 * TODO: Method level comment. 
	 * 
	 * @param readerFactoryID
	 */
	public void removeCommandConfigFactoryEvent(String readerFactoryID);

	/**
	 * TODO: Method level comment. 
	 * 
	 * @param commandConfigurationID
	 */
	public void addCommandConfiguration(String commandConfigurationID);

	/**
	 * TODO: Method level comment. 
	 * 
	 * @param commandConfigurationID
	 */
	public void removeCommandConfiguration(String commandConfigurationID);

	/**
	 * TODO: Method level comment. 
	 * 
	 * @param readerID
	 * @param sessionID
	 * @param jobID
	 * @param commandID
	 */
	public void jobSubmitted(String readerID, String sessionID, Integer jobID,
			String commandID);

	/**
	 * TODO: Method level comment. 
	 * 
	 * @param readerID
	 * @param sessionID
	 * @param jobID
	 */
	public void jobDeleted(String readerID, String sessionID, Integer jobID);

}
