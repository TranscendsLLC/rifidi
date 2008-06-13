package org.rifidi.edge.core.session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.exception.RifidiIIllegialArgumentException;
import org.rifidi.edge.core.exception.adapter.RifidiAdapterIllegalStateException;
import org.rifidi.edge.core.exception.adapter.RifidiConnectionException;
import org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo;
import org.rifidi.edge.core.readerAdapter.IReaderAdapter;
import org.rifidi.edge.core.readerAdapter.commands.ICustomCommand;
import org.rifidi.edge.core.readerAdapter.commands.ICustomCommandResult;
import org.rifidi.edge.core.session.jms.JMSMessageThread;
import org.rifidi.edge.enums.ERifidiReaderAdapter;


/**
 * 
 * @author Jerry and Kyle
 * A session bundles the objects needed to communicate to the reader.
 */

public class Session implements ISession {
	
	private static final Log logger = LogFactory.getLog(Session.class);	
	
	private IReaderAdapter adapter;
	
	private int sessionID;
	
	private AbstractConnectionInfo connectionInfo;
	
	private JMSMessageThread jmsMessageThread;
	
	private ERifidiReaderAdapter state;
	
	private Exception errorCause;
	
	/**
	 * Creates a Session.
	 * @param connectionInfo Info used to connect to a reader.
	 * @param adapter Object that talks to a reader.
	 * @param id Id for this session.
	 */
	public Session(AbstractConnectionInfo connectionInfo, IReaderAdapter adapter, int id, JMSMessageThread jmsMessageThread ){
		setConnectionInfo( connectionInfo);
		setAdapter( adapter);
		setSessionID(id);
		this.jmsMessageThread = jmsMessageThread;
	}

	/**
	 * @return the adapter
	 */
	public IReaderAdapter getAdapter() {
		return adapter;
	}

	/**
	 * @param adapter the adapter to set
	 */
	public void setAdapter(IReaderAdapter adapter) {
		this.adapter = adapter;
	}

	/**
	 * @return the sessionID
	 */
	public int getSessionID() {
		return sessionID;
	}

	/**
	 * @param sessionID the sessionID to set
	 */
	public void setSessionID(int sessionID) {
		this.sessionID = sessionID;
	}

	/**
	 * @return the connectionInfo
	 */
	public AbstractConnectionInfo getConnectionInfo() {
		return connectionInfo;
	}

	/**
	 * @param connectionInfo the connectionInfo to set
	 */
	public void setConnectionInfo(AbstractConnectionInfo connectionInfo) {
		this.connectionInfo = connectionInfo;
	}

	@Override
	public ICustomCommandResult sendCustomCommand(ICustomCommand customCommand) {
		// TODO needs to be implemented and designed
		// TODO Handle exceptions here or send them up the call chain.
		state = ERifidiReaderAdapter.BUSY;
		try {
			return adapter.sendCustomCommand(customCommand);
		} catch (RifidiAdapterIllegalStateException e) {
			state = ERifidiReaderAdapter.ERROR;
			errorCause = e;
			logger.error("Adapter in Illegal State", e);
		} catch (RifidiIIllegialArgumentException e) {
			state = ERifidiReaderAdapter.ERROR;
			errorCause = e;
			logger.error("Illegal Argument Passed.", e);
		} catch (RuntimeException e){
			/* Error Resistance.
			 * Uncaught Runtime errors should not cause the whole
			 * edge server to go down. Only that adapter that caused it.
			 * Reminder: Runtime errors in java do not need a "throws" clause to 
			 * be thrown up the stack.
			 */
			
			state = ERifidiReaderAdapter.ERROR;
			errorCause = e;
			
			logger.error("Uncaught RuntimeException in " + adapter.getClass() + " adapter. " +
						 "This means that there may be an unfixed bug in the adapter.", e);
		}
		state = ERifidiReaderAdapter.CONNECTED;
		return null;
	}

	@Override
	public void startTagStream() {
		this.jmsMessageThread.start();
		
	}

	@Override
	public void stopTagStream() {
		this.jmsMessageThread.stop();
		
	}

	//TODO: Add this to the ISession Interface... maybe?
	public ERifidiReaderAdapter getState()
	{
		return state;
	}

	//TODO: Add this to the ISession Interface... maybe?
	//TODO: Andreas, make sure this is correct.
	public void connect() {
		try {
			adapter.connect();
		} catch (RifidiConnectionException e) {
			state = ERifidiReaderAdapter.ERROR;
			errorCause = e;
			logger.error("Error while disconnecting.", e);
		} catch (RuntimeException e){
			/* Error Resistance.
			 * Uncaught Runtime errors should not cause the whole
			 * edge server to go down. Only that adapter that caused it.
			 * Reminder: Runtime errors in java do not need a "throws" clause to 
			 * be thrown up the stack.
			 */
			
			state = ERifidiReaderAdapter.ERROR;
			errorCause = e;
			
			logger.error("Uncaught RuntimeException in " + adapter.getClass() + " adapter. " +
						 "This means that there may be an unfixed bug in the adapter.", e);
		}
	}
	
	//TODO: Add this to the ISession Interface... maybe?
	//TODO: Andreas, make sure this is correct.
	public void disconnect() {
		try {
			adapter.disconnect();
		} catch (RifidiConnectionException e) {
			state = ERifidiReaderAdapter.ERROR;
			errorCause = e;
			logger.error("Error while disconnecting.", e);
		} catch (RuntimeException e){
			/* Error Resistance.
			 * Uncaught Runtime errors should not cause the whole
			 * edge server to go down. Only that adapter that caused it.
			 * Reminder: Runtime errors in java do not need a "throws" clause to 
			 * be thrown up the stack.
			 */
			
			state = ERifidiReaderAdapter.ERROR;
			errorCause = e;
			
			logger.error("Uncaught RuntimeException in " + adapter.getClass() + " adapter. " +
						 "This means that there may be an unfixed bug in the adapter.", e);
		}
	}

	/**
	 * @return The cause of the error, null if there was none.
	 */
	//TODO: Add this to the ISession Interface... maybe?
	public Exception getErrorCause() {
		return errorCause;
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
	//		return null;
	//	}
}
