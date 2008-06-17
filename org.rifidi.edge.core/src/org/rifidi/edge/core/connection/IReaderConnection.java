package org.rifidi.edge.core.connection;

import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommand;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommandResult;
import org.rifidi.edge.core.readerPlugin.enums.EReaderAdapterState;

public interface IReaderConnection {

	/**
	 * @return the adapter
	 */
	public abstract IReaderPlugin getAdapter();

	/**
	 * @param adapter
	 *            the adapter to set
	 */
	public abstract void setAdapter(IReaderPlugin adapter);

	/**
	 * @return the sessionID
	 */
	public abstract int getSessionID();

	/**
	 * @param sessionID
	 *            the sessionID to set
	 */
	public abstract void setSessionID(int sessionID);

	/**
	 * @return the connectionInfo
	 */
	public abstract AbstractReaderInfo getConnectionInfo();

	/**
	 * @param connectionInfo
	 *            the connectionInfo to set
	 */
	public abstract void setConnectionInfo(AbstractReaderInfo connectionInfo);

	public abstract ICustomCommandResult sendCustomCommand(
			ICustomCommand customCommand);

	public abstract void startTagStream();

	public abstract void stopTagStream();

	public abstract EReaderAdapterState getState();

	public abstract void connect();

	public abstract void disconnect();

	/**
	 * @return The cause of the error, null if there was none.
	 */
	public abstract Exception getErrorCause();

	/**
	 * Just for internal use
	 * 
	 * @param errorCause
	 *            the errorCause to set
	 */
	public abstract void setErrorCause(Exception errorCause);

}