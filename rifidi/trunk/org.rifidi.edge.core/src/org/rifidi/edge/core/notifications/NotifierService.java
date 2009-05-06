/**
 * 
 */
package org.rifidi.edge.core.notifications;
//TODO: Comments
import org.rifidi.edge.core.api.SessionStatus;

/**
 * @author kyle
 * 
 */
public interface NotifierService {

	public void addSessionEvent(String readerID, String sessionID);

	public void removeSessionEvent(String readerID, String sessionID);

	public void addConfigurationEvent(String serviceID);

	public void removeConfigurationEvent(String readerID);

	public void addReaderFactoryEvent(String readerFactoryID);

	public void removeReaderFactoryEvent(String readerFactoryID);

	public void sessionStatusChanged(String readerID, String sessionID,
			SessionStatus sessionStatus);

	public void addCommandConfigFactoryEvent(String readerFactoryID);

	public void removeCommandConfigFactoryEvent(String readerFactoryID);

	public void addCommandConfiguration(String commandConfigurationID);

	public void removeCommandConfiguration(String commandConfigurationID);

	public void jobSubmitted(String readerID, String sessionID, Integer jobID,
			String commandID);

	public void jobDeleted(String readerID, String sessionID, Integer jobID);

}
