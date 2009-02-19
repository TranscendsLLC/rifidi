/**
 * 
 */
package org.rifidi.edge.core.readersession;

import java.util.Set;

import org.rifidi.edge.core.exceptions.NonExistentCommandFactoryException;
import org.rifidi.edge.core.exceptions.NonExistentReaderConfigurationException;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface ReaderSessionDAO {

	/**
	 * Create and start a readersession.
	 * 
	 * @param readerConfigurationID
	 * @param commandFactoryID
	 * @throws NonExistentCommandFactoryException
	 * @throws NonExistentReaderConfigurationException
	 */
	void createAndStartReaderSession(String readerConfigurationID,
			String commandFactoryID) throws NonExistentCommandFactoryException,
			NonExistentReaderConfigurationException;
	
	/**
	 * Get the list of reader sessions.
	 * @return
	 */
	Set<String> getReaderSessions();
}
