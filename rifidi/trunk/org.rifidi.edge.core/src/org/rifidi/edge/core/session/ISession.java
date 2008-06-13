/**
 * 
 */
package org.rifidi.edge.core.session;

import org.rifidi.edge.core.readerAdapter.commands.ICustomCommand;
import org.rifidi.edge.core.readerAdapter.commands.ICustomCommandResult;

/**
 * @author jerry
 *
 */
public interface ISession {
	
	public void startTagStream();
	
	public void stopTagStream();
	
	public ICustomCommandResult sendCustomCommand(ICustomCommand customCommand);
}
