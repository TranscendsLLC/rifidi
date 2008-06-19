package org.rifidi.edge.core.connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.connection.jms.JMSMessageThread;
import org.rifidi.edge.core.exception.RifidiException;
import org.rifidi.edge.core.exception.RifidiIIllegialArgumentException;
import org.rifidi.edge.core.exception.RifidiPreviousError;
import org.rifidi.edge.core.exception.readerConnection.RifidiConnectionIllegalStateException;
import org.rifidi.edge.core.exception.readerConnection.RifidiConnectionException;
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

public class ReaderConnection implements IReaderConnection {

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

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.connection.IReaderConnection#getAdapter()
	 */
	public IReaderPlugin getAdapter() {
		return adapter;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.connection.IReaderConnection#setAdapter(org.rifidi.edge.core.readerPlugin.IReaderPlugin)
	 */
	public void setAdapter(IReaderPlugin adapter) {
		this.adapter = adapter;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.connection.IReaderConnection#getSessionID()
	 */
	public int getSessionID() {
		return sessionID;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.connection.IReaderConnection#setSessionID(int)
	 */
	public void setSessionID(int sessionID) {
		this.sessionID = sessionID;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.connection.IReaderConnection#getConnectionInfo()
	 */
	public AbstractReaderInfo getConnectionInfo() {
		return connectionInfo;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.connection.IReaderConnection#setConnectionInfo(org.rifidi.edge.core.readerPlugin.AbstractReaderInfo)
	 */
	public void setConnectionInfo(AbstractReaderInfo connectionInfo) {
		this.connectionInfo = connectionInfo;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.connection.IReaderConnection#sendCustomCommand(org.rifidi.edge.core.readerPlugin.commands.ICustomCommand)
	 */
	public ICustomCommandResult sendCustomCommand(ICustomCommand customCommand) throws RifidiException {
		// TODO needs to be implemented and designed
		// TODO Handle exceptions here or send them up the call chain.
		if (state != EReaderAdapterState.CONNECTED) {
			if (state == EReaderAdapterState.ERROR) {
				throw new RifidiPreviousError("Connection already in error state", errorCause);
			}
			RifidiException e = new RifidiConnectionIllegalStateException("Connection in illegal state.");
			setErrorCause(e);
		}
		state = EReaderAdapterState.BUSY;
		try {
			ICustomCommandResult result = adapter.sendCustomCommand(customCommand);
			state = EReaderAdapterState.CONNECTED;
			return result;
		} catch (RifidiConnectionIllegalStateException e) {
			setErrorCause(e);
			logger.error("Adapter in Illegal State", e);
		} catch (RifidiIIllegialArgumentException e) {
			setErrorCause(e);
			logger.error("Illegal Argument Passed.", e);
		} catch (RuntimeException e) {
			/*
			 * Error Resistance. Uncaught Runtime errors should not cause the
			 * whole edge server to go down. Only that adapter that caused it.
			 * Reminder: Runtime errors in java do not need a "throws" clause to
			 * be thrown up the stack.
			 */

			setErrorCause(e);

			String errorMsg = "Uncaught RuntimeException in "
				+ adapter.getClass()
				+ " adapter. "
				+ "This means that there may be an unfixed bug in the adapter.";
			logger
					.error( errorMsg, e);
			
			throw new RifidiException(errorMsg, e);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.connection.IReaderConnection#startTagStream()
	 */
	public void startTagStream() throws RifidiException {
		if (state == EReaderAdapterState.CONNECTED) {
			state = EReaderAdapterState.STREAMING;
			this.jmsMessageThread.start();
		} else {
			if (state == EReaderAdapterState.ERROR) {
				throw new RifidiPreviousError("Connection already in error state.", errorCause);
			}
			RifidiConnectionIllegalStateException e = new RifidiConnectionIllegalStateException();
			logger
					.error(
							"Adapter must be in the CONNECTED state to start the tag stream.",
							e);
			setErrorCause(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.connection.IReaderConnection#stopTagStream()
	 */
	public void stopTagStream() throws RifidiException {
		if (state == EReaderAdapterState.STREAMING) {
			state = EReaderAdapterState.CONNECTED;
			this.jmsMessageThread.stop();
		} else {
			if (state == EReaderAdapterState.ERROR) {
				throw new RifidiPreviousError("Connection already in error state.", errorCause);
			}
			RifidiConnectionIllegalStateException e = new RifidiConnectionIllegalStateException();
			logger
					.error(
							"Adapter must be in the STREAMING state to stop the tag stream.",
							e);
			setErrorCause(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.connection.IReaderConnection#getState()
	 */
	public EReaderAdapterState getState() {
		return state;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.connection.IReaderConnection#connect()
	 */
	public void connect() throws RifidiException {
		if (state != EReaderAdapterState.CREATED) {
			if (state == EReaderAdapterState.ERROR) {
				logger.debug("Trying to recconnect after an error occured...");
				errorCause= null;
			} else {
				RifidiException e =  new RifidiConnectionIllegalStateException("Addapter trying to connect in illegal state.");
				setErrorCause(e);
				throw e;
			}
		}
		try {
			adapter.connect();
			state = EReaderAdapterState.CONNECTED;
		} catch (RifidiConnectionException e) {
			setErrorCause(e);
			logger.error("Error while connecting.", e);
			throw e;
		} catch (RuntimeException e) {
			/*
			 * Error Resistance. Uncaught Runtime errors should not cause the
			 * whole edge server to go down. Only that adapter that caused it.
			 * Reminder: Runtime errors in java do not need a "throws" clause to
			 * be thrown up the stack.
			 */

			setErrorCause(e);

			String errorMsg = "Uncaught RuntimeException in "
				+ adapter.getClass()
				+ " adapter. "
				+ "This means that there may be an unfixed bug in the adapter.";
			logger
					.error( errorMsg, e);
			
			throw new RifidiException(errorMsg, e);
		}
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.connection.IReaderConnection#disconnect()
	 */
	public void disconnect() throws RifidiException {
		if (state != EReaderAdapterState.CONNECTED){
			if (state == EReaderAdapterState.ERROR) {
				throw new RifidiPreviousError("Connection already in error state.", errorCause);
			}
			RifidiException e = new RifidiConnectionIllegalStateException("Connection in illegal state while trying to disconnect");
			setErrorCause(e);
			throw e;
		}
		try {
			adapter.disconnect();
			state = EReaderAdapterState.DISCONECTED;
		} catch (RifidiConnectionException e) {
			setErrorCause(e);
			logger.error("Error while disconnecting.", e);
		} catch (RuntimeException e) {
			/*
			 * Error Resistance. Uncaught Runtime errors should not cause the
			 * whole edge server to go down. Only that adapter that caused it.
			 * Reminder: Runtime errors in java do not need a "throws" clause to
			 * be thrown up the stack.
			 */

			setErrorCause(e);

			String errorMsg = "Uncaught RuntimeException in "
				+ adapter.getClass()
				+ " adapter. "
				+ "This means that there may be an unfixed bug in the adapter.";
			logger
					.error( errorMsg, e);
			
			throw new RifidiException(errorMsg, e);
		}
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.connection.IReaderConnection#getErrorCause()
	 */
	public Exception getErrorCause() {
		return errorCause;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.connection.IReaderConnection#setErrorCause(java.lang.Exception)
	 */
	public void setErrorCause(Exception errorCause) {
		
		if (errorCause != null ){
			//Need to do some house keeping first...
			try {
				this.jmsMessageThread.stop();
				adapter.disconnect();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			this.errorCause = errorCause;
			state = EReaderAdapterState.ERROR;
		}
	}

	public void cleanUp() {
		jmsMessageThread.stop();
		jmsMessageThread = null;
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
