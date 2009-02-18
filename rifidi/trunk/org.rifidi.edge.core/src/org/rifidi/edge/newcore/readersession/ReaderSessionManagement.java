/**
 * 
 */
package org.rifidi.edge.newcore.readersession;

import org.rifidi.edge.newcore.exceptions.NonExistentCommandFactoryException;
import org.rifidi.edge.newcore.exceptions.NonExistentReaderConfigurationException;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface ReaderSessionManagement {

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

}
