package org.rifidi.edge.core.connection;

import org.rifidi.edge.core.exception.RifidiException;
import org.rifidi.edge.core.exception.RifidiIIllegialArgumentException;
import org.rifidi.edge.core.exception.readerConnection.RifidiConnectionIllegalStateException;
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
			ICustomCommand customCommand) throws RifidiException;

	/**
	 * Starts the tag streaming
	 */
	public abstract void startTagStream() throws RifidiException;

	/**
	 * Stops the tag streaming.
	 */
	public abstract void stopTagStream() throws RifidiException;

	/**
	 * @return The currect state of the reader.
	 */
	public abstract EReaderAdapterState getState();

	/**
	 * Connect to the reader
	 */
	public abstract void connect() throws RifidiException;

	/**
	 * Disconnect from the reader
	 */
	public abstract void disconnect() throws RifidiException;

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