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

	/**
	 * @param customCommand
	 * @return
	 */
	public abstract ICustomCommandResult sendCustomCommand(
			ICustomCommand customCommand);

	/**
	 * Starts the tag streaming
	 */
	public abstract void startTagStream();

	/**
	 * Stops the tag streaming.
	 */
	public abstract void stopTagStream();

	/**
	 * @return The currect state of the reader.
	 */
	public abstract EReaderAdapterState getState();

	/**
	 * Connect to the reader
	 */
	public abstract void connect();

	/**
	 * Disconnect from the reader
	 */
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