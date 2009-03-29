/**
 * 
 */
package org.rifidi.edge.core.notifications;

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

}
