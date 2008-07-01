/**
 * 
 */
package org.rifidi.edge.core.connection.listener;

import org.rifidi.edge.core.connection.impl.ReaderConnection;



/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
public interface ReaderConnectionListener {
	public void readerConnectionRegistryAddEvent(
			ReaderConnection connection);

	public void readerConnectionRegistryRemoveEvent(
			ReaderConnection connection);

}
