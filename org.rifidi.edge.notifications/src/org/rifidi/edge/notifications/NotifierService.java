/**
 * 
 */
package org.rifidi.edge.notifications;

/**
 * @author kyle
 * 
 */
public interface NotifierService {

	public void addSessionEvent(String readerID, String sessionID);

	public void removeSessionEvent(String readerID, String sessionID);

	public void addReaderEvent(String readerID);

	public void removeReaderEvent(String readerID);

	public void addReaderFactoryEvent(String readerFactoryID);

	public void removeReaderFactoryEvent(String readerFactoryID);

}
