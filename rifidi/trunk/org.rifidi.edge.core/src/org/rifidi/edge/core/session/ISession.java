/**
 * 
 */
package org.rifidi.edge.core.session;

import org.rifidi.edge.core.readerAdapter.commands.ICustomCommand;
import org.rifidi.edge.core.readerAdapter.commands.ICustomCommandResult;
import org.rifidi.edge.enums.EReaderAdapterState;

/**
 * @author jerry
 *
 */
public interface ISession {
	
	public void connect();
	
	public void disconnect();
	
	public void startTagStream();
	
	public void stopTagStream();
	
	public ICustomCommandResult sendCustomCommand(ICustomCommand customCommand);
	
	public Exception getErrorCause();
	
	public EReaderAdapterState getState();
}
