/**
 * 
 */
package org.rifidi.edge.core.session;

import org.rifidi.edge.core.readerAdapter.commands.ICustomCommand;

/**
 * @author jerry
 *
 */
public interface ISession {
	
	public void startTagStream();
	
	public void stopTagStream();
	
	public void sendCustomCommand(ICustomCommand customCommand);
}
