/*
 *  ReaderConnection.java
 *
 *  Created:	Jun 19, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.core.connection;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.buffer.ConnectionBuffer;
import org.rifidi.edge.core.communication.protocol.Protocol;
import org.rifidi.edge.core.communication.service.CommunicationService;
import org.rifidi.edge.core.connection.jms.JMSService;
import org.rifidi.edge.core.exception.RifidiException;
import org.rifidi.edge.core.exception.RifidiIIllegialArgumentException;
import org.rifidi.edge.core.exception.RifidiPreviousErrorException;
import org.rifidi.edge.core.exception.readerConnection.RifidiConnectionException;
import org.rifidi.edge.core.exception.readerConnection.RifidiConnectionIllegalStateException;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommand;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommandResult;
import org.rifidi.edge.core.readerPlugin.enums.EReaderAdapterState;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * 
 * @author Jerry and Kyle A session bundles the objects needed to communicate to
 *         the reader.
 */

public class ReaderConnection implements IReaderConnection {

	private static final Log logger = LogFactory.getLog(ReaderConnection.class);

	private IReaderPlugin plugin;

	// The id of this connection
	private int connectionID;

	// The reader info for the specific plugin
	private AbstractReaderInfo connectionInfo;

	// The current state of this connection.
	private EReaderAdapterState state;

	// What caused the error in the adapter
	private RifidiException errorCause;
	// Semaphore for the above variable 
	private Object errorSempaphore = new Object();
	
	private JMSService jmsService;

	private CommunicationService communicationService;
	private Protocol protocol;

	private ConnectionBuffer connectionBuffer;

	//TODO Andreas: Need to change the name of this method.
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
			IReaderPlugin adapter, Protocol protocol, int id) {
		setConnectionInfo(connectionInfo);
		setAdapter(adapter);
		setSessionID(id);
		state = EReaderAdapterState.CREATED;
		ServiceRegistry.getInstance().service(this);

		this.protocol = protocol;
	}

	//TODO Andreas: Need to change the name of this method.
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.connection.IReaderConnection#getAdapter()
	 */
	public IReaderPlugin getAdapter() {
		return plugin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.connection.IReaderConnection#setAdapter(org.rifidi.edge.core.readerPlugin.IReaderPlugin)
	 */
	public void setAdapter(IReaderPlugin adapter) {
		this.plugin = adapter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.connection.IReaderConnection#getSessionID()
	 */
	public int getSessionID() {
		return connectionID;
	}

	// TODO Andreas: Need to change the name of this method.
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.connection.IReaderConnection#setSessionID(int)
	 */
	public void setSessionID(int sessionID) {
		this.connectionID = sessionID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.connection.IReaderConnection#getConnectionInfo()
	 */
	public AbstractReaderInfo getConnectionInfo() {
		return connectionInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.connection.IReaderConnection#setConnectionInfo(org.rifidi.edge.core.readerPlugin.AbstractReaderInfo)
	 */
	public void setConnectionInfo(AbstractReaderInfo connectionInfo) {
		this.connectionInfo = connectionInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.connection.IReaderConnection#sendCustomCommand(org.rifidi.edge.core.readerPlugin.commands.ICustomCommand)
	 */
	public ICustomCommandResult sendCustomCommand(ICustomCommand customCommand)
			throws RifidiException {

		if (state != EReaderAdapterState.CONNECTED) {
			if (state == EReaderAdapterState.ERROR) {
				throw new RifidiPreviousErrorException(
						"Connection already in error state.", errorCause);
			} else {
				RifidiException e = new RifidiConnectionIllegalStateException(
						"Addapter trying to connect in illegal state.");
				setErrorCause(e);
				throw e;
			}
		}

		state = EReaderAdapterState.BUSY;
		try {
			ICustomCommandResult result = plugin
					.sendCustomCommand(customCommand);
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

			String errorMsg = "Uncaught RuntimeException in "
					+ plugin.getClass()
					+ " adapter. "
					+ "This means that there may be an unfixed bug in the adapter.";

			logger.error(errorMsg, e);

			/* We wrap the Runtime exception */
			RifidiException e2 = new RifidiException(errorMsg, e);

			setErrorCause(e2);

			/* And throw it. */
			throw e2;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.connection.IReaderConnection#startTagStream()
	 */
	public void startTagStream() throws RifidiException {
		if (state == EReaderAdapterState.CONNECTED) {
			state = EReaderAdapterState.STREAMING;
			// this.jmsMessageThread.start();
			jmsService.register(this);
		} else {
			if (state == EReaderAdapterState.ERROR) {
				throw new RifidiPreviousErrorException(
						"Connection already in error state.", errorCause);
			}
			RifidiConnectionIllegalStateException e = new RifidiConnectionIllegalStateException();
			logger
					.error(
							"Adapter must be in the CONNECTED state to start the tag stream.",
							e);
			setErrorCause(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.connection.IReaderConnection#stopTagStream()
	 */
	public void stopTagStream() throws RifidiException {
		if (state == EReaderAdapterState.STREAMING) {
			state = EReaderAdapterState.CONNECTED;
			// this.jmsMessageThread.stop();
			jmsService.unregister(this);
		} else {
			if (state == EReaderAdapterState.ERROR) {
				throw new RifidiPreviousErrorException(
						"Connection already in error state.", errorCause);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.connection.IReaderConnection#getState()
	 */
	public EReaderAdapterState getState() {
		synchronized(errorSempaphore){
			return state;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.connection.IReaderConnection#connect()
	 */
	public void connect() throws RifidiException {
		if (state != EReaderAdapterState.CREATED) {
			if (state == EReaderAdapterState.ERROR) {
				logger.debug("Trying to recconnect after an error occured...");
				errorCause = null;
			} else if (state == EReaderAdapterState.DISCONECTED) {
				logger.debug("Reconnecting from being disconnected...");
			} else {
				RifidiException e = new RifidiConnectionIllegalStateException(
						"Addapter trying to connect in illegal state.");
				setErrorCause(e);
				throw e;
			}
		}
		try {
				if (communicationService == null)
					throw new ConnectException("Communication Service not found");
			
				connectionBuffer = communicationService
						.createConnection(plugin, connectionInfo, protocol);
				state = EReaderAdapterState.CONNECTED;

				plugin.connect(connectionBuffer);

		} catch (ConnectException e) {
			setErrorCause(new RifidiConnectionException("Error while connecting",e));
		} catch (UnknownHostException e) {
			setErrorCause(new RifidiConnectionException("Error while connecting",e));
		} catch (IOException e) {
			setErrorCause(new RifidiConnectionException("Error while connecting",e));
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

			String errorMsg = "Uncaught RuntimeException in "
					+ plugin.getClass()
					+ " adapter. "
					+ "This means that there may be an unfixed bug in the adapter.";

			logger.error(errorMsg, e);

			/* We wrap the Runtime exception */
			RifidiException e2 = new RifidiException(errorMsg, e);

			setErrorCause(e2);

			/* And throw it. */
			throw e2;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.connection.IReaderConnection#disconnect()
	 */
	public void disconnect() throws RifidiException {
		if (! ((state == EReaderAdapterState.CONNECTED)
				|| (state == EReaderAdapterState.CREATED)) ) {
			if (state == EReaderAdapterState.ERROR) {
				throw new RifidiPreviousErrorException(
						"Connection already in error state and already disconnected.",
						errorCause);
			}
			
			RifidiException e = new RifidiConnectionException(
					"Connection in illegal state while trying to disconnect");
			setErrorCause(e);
			throw e;
		}
		try {
			plugin.disconnect();
			try {
				communicationService.destroyConnection(connectionBuffer);
			} catch (IOException e) {
				setErrorCause(new RifidiConnectionException(e));
				logger.error("Error while disconnecting", e);
			}
			
			
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

			String errorMsg = "Uncaught RuntimeException in "
					+ plugin.getClass()
					+ " adapter. "
					+ "This means that there may be an unfixed bug in the adapter.";

			logger.error(errorMsg, e);

			/* We wrap the Runtime exception */
			RifidiException e2 = new RifidiException(errorMsg, e);

			setErrorCause(e2);

			/* And throw it. */
			throw e2;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.connection.IReaderConnection#getErrorCause()
	 */
	public Exception getErrorCause() {
		logger.debug("Getting error Cause.");
		Exception e = null;
		synchronized (errorSempaphore) {
			e = errorCause;
		}
		return e;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.connection.IReaderConnection#setErrorCause(java.lang.Exception)
	 */
	public void setErrorCause(RifidiException errorCause) {
		synchronized(errorSempaphore){
			if (errorCause != null) {
				// Need to do some house keeping first...
				try {
					// this.jmsMessageThread.stop();
					jmsService.unregister(this);
					plugin.disconnect();
				} catch (Exception e) {
					// e.printStackTrace();
	
					/*
					 * we ignore any exceptions because we are already in an error
					 * state
					 */
				}
	
				this.errorCause = errorCause;
				state = EReaderAdapterState.ERROR;
				logger.debug("Set reader state to error with message: "
						+ errorCause.getClass().getSimpleName());
			}
			errorSempaphore.notify();
		}
	}

	public void cleanUp() {
		jmsService.unregister(this);
		// jmsMessageThread.stop();
		// jmsMessageThread = null;
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

	@Inject
	public void setJMSService(JMSService jmsService) {
		this.jmsService = jmsService;
	}

	@Inject
	public void setCommunicationService(
			CommunicationService communicationService) {
		this.communicationService = communicationService;
	}

}
