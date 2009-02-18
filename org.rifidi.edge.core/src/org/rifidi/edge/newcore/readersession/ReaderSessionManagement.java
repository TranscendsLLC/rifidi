/**
 * 
 */
package org.rifidi.edge.newcore.readersession;

import java.util.Set;

import org.rifidi.edge.newcore.commands.CommandFactory;
import org.rifidi.edge.newcore.readers.ReaderConfiguration;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface ReaderSessionManagement {

	/**
	 * Create and start a readersession.
	 * 
	 * @param reader
	 * @param command
	 */
	void createAndStartReaderSession(ReaderConfiguration<?> reader,
			CommandFactory<?> command);

	/**
	 * Return the set of currently available readers.
	 * 
	 * @return
	 */
	Set<ReaderConfiguration<?>> getAvailableReaderConfigurations();

	/**
	 * Return the set of currently available command factories.
	 * 
	 * @return
	 */
	Set<CommandFactory<?>> getAvailableCommandFactories();

}
