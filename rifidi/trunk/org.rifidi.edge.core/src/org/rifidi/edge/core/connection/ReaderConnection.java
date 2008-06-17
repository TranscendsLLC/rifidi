package org.rifidi.edge.core.connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.connection.jms.JMSMessageThread;
import org.rifidi.edge.core.exception.RifidiAdapterIllegalStateException;
import org.rifidi.edge.core.exception.RifidiConnectionException;
import org.rifidi.edge.core.exception.RifidiIIllegialArgumentException;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommand;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommandResult;
import org.rifidi.edge.core.readerPlugin.enums.EReaderAdapterState;

/**
 * 
 * @author Jerry and Kyle A session bundles the objects needed to communicate to
 *         the reader.
 */

public class ReaderConnection {

	private static final Log logger = LogFactory.getLog(ReaderConnection.class);

	private IReaderPlugin adapter;

	private int sessionID;

	private AbstractReaderInfo connectionInfo;

	private JMSMessageThread jmsMessageThread;

	private EReaderAdapterState state;

	private Exception errorCause;

	/**
	 * Creates a Session.
	 * 
	 * @param connectionInfo
	 *            Info used to connect to a reader.
	 * @param adapter
	 *            Object that talks to a reader.
	 * @param id
	 *            Id for this session.
	 */
	public ReaderConnection(AbstractReaderInfo connectionInfo,
			IReaderPlugin adapter, int id, JMSMessageThread jmsMessageThread) {
		setConnectionInfo(connectionInfo);
		setAdapter(adapter);
		setSessionID(id);
		this.jmsMessageThread = jmsMessageThread;
		state = EReaderAdapterState.CREATED;
	}

	/**
	 * @return the adapter
	 */
	public IReaderPlugin getAdapter() {
		return adapter;
	}

	/**
	 * @param adapter
	 *            the adapter to set
	 */
	public void setAdapter(IReaderPlugin adapter) {
		this.adapter = adapter;
	}

	/**
	 * @return the sessionID
	 */
	public int getSessionID() {
		return sessionID;
	}

	/**
	 * @param sessionID
	 *            the sessionID to set
	 */
	public void setSessionID(int sessionID) {
		this.sessionID = sessionID;
	}

	/**
	 * @return the connectionInfo
	 */
	public AbstractReaderInfo getConnectionInfo() {
		return connectionInfo;
	}

	/**
	 * @param connectionInfo
	 *            the connectionInfo to set
	 */
	public void setConnectionInfo(AbstractReaderInfo connectionInfo) {
		this.connectionInfo = connectionInfo;
	}

	public ICustomCommandResult sendCustomCommand(ICustomCommand customCommand) {
		// TODO needs to be implemented and designed
		// TODO Handle exceptions here or send them up the call chain.
		state = EReaderAdapterState.BUSY;
		try {
			ICustomCommandResult result = adapter.sendCustomCommand(customCommand);
			state = EReaderAdapterState.CONNECTED;
			return result;
		} catch (RifidiAdapterIllegalStateException e) {
			state = EReaderAdapterState.ERROR;
			errorCause = e;
			logger.error("Adapter in Illegal State", e);
		} catch (RifidiIIllegialArgumentException e) {
			state = EReaderAdapterState.ERROR;
			errorCause = e;
			logger.error("Illegal Argument Passed.", e);
		} catch (RuntimeException e) {
			/*
			 * Error Resistance. Uncaught Runtime errors should not cause the
			 * whole edge server to go down. Only that adapter that caused it.
			 * Reminder: Runtime errors in java do not need a "throws" clause to
			 * be thrown up the stack.
			 */

			state = EReaderAdapterState.ERROR;
			errorCause = e;

			logger
					.error(
							"Uncaught RuntimeException in "
									+ adapter.getClass()
									+ " adapter. "
									+ "This means that there may be an unfixed bug in the adapter.",
							e);
		}
		return null;
	}

	public void startTagStream() {
		if (state == EReaderAdapterState.CONNECTED) {
			state = EReaderAdapterState.STREAMING;
			this.jmsMessageThread.start();
		} else {
			state = EReaderAdapterState.ERROR;
			RifidiAdapterIllegalStateException e = new RifidiAdapterIllegalStateException();
			logger
					.error(
							"Adapter must be in the CONNECTED state to start the tag stream.",
							e);
			errorCause = e;
		}
	}

	public void stopTagStream() {
		if (state == EReaderAdapterState.STREAMING) {
			state = EReaderAdapterState.CONNECTED;
			this.jmsMessageThread.stop();
		} else {
			state = EReaderAdapterState.ERROR;
			RifidiAdapterIllegalStateException e = new RifidiAdapterIllegalStateException();
			logger
					.error(
							"Adapter must be in the STREAMING state to stop the tag stream.",
							e);
			errorCause = e;
		}
	}

	public EReaderAdapterState getState() {
		return state;
	}

	public void connect() {
		try {
			adapter.connect();
			state = EReaderAdapterState.CONNECTED;
		} catch (RifidiConnectionException e) {
			state = EReaderAdapterState.ERROR;
			errorCause = e;
			logger.error("Error while connecting.", e);
		} catch (RuntimeException e) {
			/*
			 * Error Resistance. Uncaught Runtime errors should not cause the
			 * whole edge server to go down. Only that adapter that caused it.
			 * Reminder: Runtime errors in java do not need a "throws" clause to
			 * be thrown up the stack.
			 */

			state = EReaderAdapterState.ERROR;
			errorCause = e;

			logger
					.error(
							"Uncaught RuntimeException in "
									+ adapter.getClass()
									+ " adapter. "
									+ "This means that there may be an unfixed bug in the adapter.",
							e);
		}
	}

	public void disconnect() {
		try {
			adapter.disconnect();
			state = EReaderAdapterState.DISCONECTED;
		} catch (RifidiConnectionException e) {
			state = EReaderAdapterState.ERROR;
			errorCause = e;
			logger.error("Error while disconnecting.", e);
		} catch (RuntimeException e) {
			/*
			 * Error Resistance. Uncaught Runtime errors should not cause the
			 * whole edge server to go down. Only that adapter that caused it.
			 * Reminder: Runtime errors in java do not need a "throws" clause to
			 * be thrown up the stack.
			 */

			state = EReaderAdapterState.ERROR;
			errorCause = e;

			logger
					.error(
							"Uncaught RuntimeException in "
									+ adapter.getClass()
									+ " adapter. "
									+ "This means that there may be an unfixed bug in the adapter.",
							e);
		}
	}

	/**
	 * @return The cause of the error, null if there was none.
	 */
	public Exception getErrorCause() {
		return errorCause;
	}

	/**
	 * Just for internal use
	 * 
	 * @param errorCause
	 *            the errorCause to set
	 */
	public void setErrorCause(Exception errorCause) {
		this.errorCause = errorCause;
		state = EReaderAdapterState.ERROR;
	}

	// //TODO: Think if we need this method.
	// public List<TagRead> getNextTags() {
	// try {
	// return adapter.getNextTags();
	// } catch (RifidiAdapterIllegalStateException e) {
	// state = ERifidiReaderAdapter.ERROR;
	// errorCause = e;
	// logger.error("Error while getting tags.", e);
	// } catch (RuntimeException e){
	// /* Error Resistance.
	// * Uncaught Runtime errors should not cause the whole
	// * edge server to go down. Only that adapter that caused it.
	// * Reminder: Runtime errors in java do not need a "throws" clause to
	// * be thrown up the stack.
	// */
	//			
	// state = ERifidiReaderAdapter.ERROR;
	// errorCause = e;
	//			
	// logger.error("Uncaught RuntimeException in " + adapter.getClass() + "
	// adapter. " +
	// "This means that there may be an unfixed bug in the adapter.", e);
	// }
	// return null;
	// }
}
