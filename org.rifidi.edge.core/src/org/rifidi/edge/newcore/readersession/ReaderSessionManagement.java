/**
 * 
 */
package org.rifidi.edge.newcore.readersession;

import java.util.Set;

import org.rifidi.edge.newcore.ReaderConfiguration;
import org.rifidi.edge.newcore.internal.ReaderSession;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface ReaderSessionManagement {
	/**
	 * Create an empty reader session and register it to OSGi. It will have an
	 * "id" parameter associated with it to locate it in the registry.
	 * 
	 * @return
	 */
	ReaderSession createReaderSession();

	/**
	 * Return the set of currently available readers.
	 * 
	 * @return
	 */
	Set<ReaderConfiguration> getAvailableReaderConfigurations();

}
